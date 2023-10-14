package edu.upenn.cit594.datamanagement;

import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.util.Properties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PropertyReader {
    static String filePath;

    static Map<String, ArrayList<Properties>> records;

    public PropertyReader(String propertiesFile) {
        records = new HashMap<>();
        filePath = propertiesFile;
    }

    public Map<String, ArrayList<Properties>> returnPropertyMap() {

        if (filePath == null) {
            return records;
        } else {
            Logger.getInstance().log(filePath);
        }

        try (var reader = new CharacterReader(filePath)) {
            var csvReader = new CSVReader(reader);
            String[] searchName = new String[]{"zip_code", "market_value", "total_livable_area"};

            String[] headerColumns = csvReader.readRow();

            if (headerColumns == null) {
                return records;
            }

            Map<String, Integer> index = DataReader.searchIndex(headerColumns, searchName);

            int zipCodeIndex = index.get("zip_code");
            int marketValueIndex = index.get("market_value");
            int totalLivableAreaIndex = index.get("total_livable_area");

            String[] values;
            while ((values = csvReader.readRow()) != null) {

                if (zipCodeIndex > values.length || marketValueIndex > values.length || totalLivableAreaIndex > values.length) {
                    continue;
                }

                String zipCode = values[zipCodeIndex];
                String marketValue = values[marketValueIndex];
                String totalLivableArea = values[totalLivableAreaIndex];

                zipCode = DataReader.cutString(zipCode, 5);

                // Check if the first 5 characters of ZIP code are numeric
                if (!DataReader.isValidZipCode(zipCode)) {
                    continue;
                }

                // Check if market value is numeric
                if (!marketValue.matches("-?\\d+(\\.\\d+)?")) {
                    marketValue = "";
                }

                // Check if total livable area is numeric
                if (!totalLivableArea.matches("\\d+(\\.\\d+)?")) {
                    totalLivableArea = "";
                }

                Properties record = new Properties();

                record.setMarketValue(stringToDouble(marketValue));

                record.setTotalLivableArea(stringToDouble(totalLivableArea));

                records.putIfAbsent(zipCode, new ArrayList<>());
                records.get(zipCode).add(record);

            }

        } catch (IOException | CSVFormatException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return records;
    }

    private static Double stringToDouble(String input) {
        // Return null if the input string is empty
        if (input == null || input.isEmpty()) {
            return null;
        }

        // Convert the input string to a double
        return Double.parseDouble(input);
    }

}
