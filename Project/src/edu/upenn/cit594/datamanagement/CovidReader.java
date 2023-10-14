package edu.upenn.cit594.datamanagement;

import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.util.Vaccination;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CovidReader {

    static Map<String, ArrayList<Vaccination>> records;

    static String filePath;
    Pattern isValidTimestamp = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$");

    public CovidReader(String fileName) {
        records = new HashMap<>();
        filePath = fileName;
    }

    public Map<String, ArrayList<Vaccination>> returnCovidMap() {
        if (filePath == null) {
            return records;
        } else if (filePath.endsWith(".csv")) {
           Logger.getInstance().log(filePath);
            CSVReader();
        } else if (filePath.endsWith(".json")) {
            Logger.getInstance().log(filePath);
            JsonReader();
        } else {
            System.out.println("Invalid file format");
        }
        return records;
    }

    private void JsonReader() {

        JSONParser parser = new JSONParser();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            JSONArray jsonArray = (JSONArray) parser.parse(br);

            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                String zipCode = String.valueOf(jsonObject.get("zip_code"));

                if (DataReader.isValidZipCode(zipCode) && isValidTimestamp.matcher((String) jsonObject.get("etl_timestamp")).matches()) {
                    int partiallyVaccinated = 0;
                    int fullyVaccinated = 0;

                    String partialVaccineString = String.valueOf(jsonObject.get("partially_vaccinated"));
                    String fullVaccineString = String.valueOf(jsonObject.get("fully_vaccinated"));

                    if (partialVaccineString != null && partialVaccineString != "null" && !partialVaccineString.isEmpty()) {
                        partiallyVaccinated = Integer.parseInt(partialVaccineString);
                    }

                    if (fullVaccineString != null && partialVaccineString != "null" && !fullVaccineString.isEmpty()) {
                        fullyVaccinated = Integer.parseInt(fullVaccineString);
                    }

                    Vaccination record = new Vaccination();

                    record.setDate((String) jsonObject.get("etl_timestamp"));

                    record.setPartiallyVaccinated(partiallyVaccinated);

                    record.setFullyVaccinated(fullyVaccinated);

                    records.putIfAbsent(zipCode, new ArrayList<>());
                    records.get(zipCode).add(record);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void CSVReader() {
        try (var reader = new CharacterReader(filePath)) {
            var csvReader = new CSVReader(reader);

            String[] searchName = new String[]{"partially_vaccinated", "zip_code", "fully_vaccinated", "etl_timestamp"};

            String[] headerColumns = csvReader.readRow();
            if (headerColumns == null) {
                return;
            }

            Map<String, Integer> index = DataReader.searchIndex(headerColumns, searchName);

            int zipCodeIndex = index.get("zip_code");
            int partiallyVaccinatedIndex = index.get("partially_vaccinated");
            int fullyVaccinatedIndex = index.get("fully_vaccinated");
            int isValidTimestampIndex = index.get("etl_timestamp");

            String[] data;
            while ((data = csvReader.readRow()) != null) {

                String zipCode = data[zipCodeIndex];

                if (DataReader.isValidZipCode(zipCode) && isValidTimestamp.matcher(data[isValidTimestampIndex]).matches()) {
                    int partiallyVaccinated;
                    int fullyVaccinated;

                    if (data[partiallyVaccinatedIndex].isEmpty() || data[partiallyVaccinatedIndex].equals(" ")) {
                        partiallyVaccinated = 0;
                    } else {
                        partiallyVaccinated = Integer.parseInt(data[partiallyVaccinatedIndex]);
                    }

                    if (data[fullyVaccinatedIndex].isEmpty() || data[fullyVaccinatedIndex].equals(" ")) {
                        fullyVaccinated = 0;
                    } else {
                        fullyVaccinated = Integer.parseInt(data[fullyVaccinatedIndex]);
                    }

                    Vaccination record = new Vaccination();

                    record.setDate(data[isValidTimestampIndex]);

                    record.setPartiallyVaccinated(partiallyVaccinated);

                    record.setFullyVaccinated(fullyVaccinated);

                    records.putIfAbsent(data[zipCodeIndex], new ArrayList<>());
                    records.get(data[zipCodeIndex]).add(record);
                }
            }

        } catch (IOException | CSVFormatException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
