package edu.upenn.cit594.datamanagement;

import edu.upenn.cit594.logging.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PopulationReader {
    static String filePath;

    Map<String, Integer> records;
    Pattern populationPattern = Pattern.compile("^\\d+$");

    public PopulationReader(String populationFile) {
        records = new HashMap<>();
        filePath = populationFile;
    }

    public Map<String, Integer> returnPopulationMap() {

        if (filePath == null) {
            return records;
        } else {
            Logger.getInstance().log(filePath);
        }


        try (var reader = new CharacterReader(filePath)) {
            var csvReader = new CSVReader(reader);

            String[] searchName = new String[]{"zip_code", "population"};

            String[] headerColumns = csvReader.readRow();

            if (headerColumns == null) {
                return records;
            }

            Map<String, Integer> index = DataReader.searchIndex(headerColumns, searchName);

            int zipCodeIndex = index.get("zip_code");
            int populationIndex = index.get("population");

            String[] values;
            while ((values = csvReader.readRow()) != null) {

                String zipCode = values[zipCodeIndex];

                if (DataReader.isValidZipCode(zipCode) && populationPattern.matcher(values[populationIndex]).matches()) {
                    // Add zip code and population to the map
                    records.put(zipCode, Integer.parseInt(values[populationIndex]));
                }
            }

        } catch (IOException | CSVFormatException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return records;
    }
}
