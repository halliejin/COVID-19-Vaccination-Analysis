package edu.upenn.cit594.datamanagement;

import edu.upenn.cit594.util.Vaccination;
import edu.upenn.cit594.util.Properties;
import edu.upenn.cit594.util.Population;

import java.util.*;
import java.util.regex.Pattern;

public class DataReader {
    // Member variables to store file paths and data
    private ArrayList<Population> populationList;
    private String populationFilePath;
    private String covidFilePath;
    private String propertyFilePath;

    private boolean populationDataAvailable;
    private boolean propertyDataAvailable;
    private boolean vaccinationDataAvailable;

    private Set<String> zipCodeSet;
    private Map<String, ArrayList<Vaccination>> vaccinationData;
    private Map<String, ArrayList<Properties>> propertyData;
    private Map<String, Integer> populationData;

    // Constructor to initialize paths and collections
    public DataReader(String popFile, String covFile, String propFile) {
        populationList = new ArrayList<>();
        zipCodeSet = new HashSet<>();
        populationDataAvailable = false;
        propertyDataAvailable = false;
        vaccinationDataAvailable = false;

        populationFilePath = popFile;
        covidFilePath = covFile;
        propertyFilePath = propFile;
    }

    // Getters for data availability flags
    public boolean getHasPopulationData() {
        return populationDataAvailable;
    }

    public boolean getHasPropertyData() {
        return propertyDataAvailable;
    }

    public boolean getHasVaccinationData() {
        return vaccinationDataAvailable;
    }

    // Main method to update ZIP codes data from different sources
    public void updateListZipCodes() {
        readData();
        updateZipCodes(vaccinationData, "covid");
        updateZipCodes(propertyData, "property");
        updateZipCodes(populationData, "population");
    }

    // Read data from respective readers
    private void readData() {
        vaccinationData = new CovidReader(covidFilePath).returnCovidMap();
        propertyData = new PropertyReader(propertyFilePath).returnPropertyMap();
        populationData = new PopulationReader(populationFilePath).returnPopulationMap();
    }

    // Iterate through the given map and update ZIP codes
    private void updateZipCodes(Map<String, ?> dataMap, String dataType) {
        for (String zip : dataMap.keySet()) {
            updatePopulationData(zip, dataMap.get(zip), dataType);
        }
    }

    // Update individual population data
    private void updatePopulationData(String zipCode, Object data, String dataType) {
        Population population = getOrCreatePopulation(zipCode);

        switch (dataType) {
            case "covid":
                population.setDailyVaccinations((ArrayList<Vaccination>) data);
                break;
            case "property":
                population.setPropertyInfos((ArrayList<Properties>) data);
                break;
            case "population":
                population.setPopulation((Integer) data);
                break;
        }
    }

    // Get existing or create a new population object for the given ZIP code
    private Population getOrCreatePopulation(String zipCode) {
        Population population;
        if (!zipCodeSet.contains(zipCode)) {
            population = new Population(zipCode);
            zipCodeSet.add(zipCode);
            populationList.add(population);
        } else {
            population = populationList.stream().filter(z -> z.getZipCode().equals(zipCode)).findFirst().orElse(null);
        }
        return population;
    }

    // Get all ZIP codes, and if data not loaded, load it
    public ArrayList<Population> getAllZipCodes() {
        if (populationList.isEmpty()) {
            updateListZipCodes();
            updateDataAvailability();
        }
        return populationList;
    }

    // Update flags indicating the availability of different data
    private void updateDataAvailability() {
        vaccinationDataAvailable = !vaccinationData.isEmpty();
        populationDataAvailable = !populationData.isEmpty();
        propertyDataAvailable = !propertyData.isEmpty();
    }

    // Existing static methods
    static Map<String, Integer> searchIndex(String[] headerColumns, String[] nameArray) {
        Map<String, Integer> indexArray = new HashMap<>();
        for (String name : nameArray) {
            for (int i = 0; i < headerColumns.length; i++) {
                if (headerColumns[i].equals(name)) {
                    indexArray.put(name, i);
                }
            }
        }
        return indexArray;
    }

    public static String cutString(String zipCode, int length) {
        if (zipCode.length() >= length) {
            zipCode = zipCode.substring(0, length);
            return zipCode;
        } else {
            return "WRONG";
        }
    }

    public static boolean isValidZipCode(String zipCode) {
        return Pattern.matches("^\\d{5}$", zipCode);
    }
}

