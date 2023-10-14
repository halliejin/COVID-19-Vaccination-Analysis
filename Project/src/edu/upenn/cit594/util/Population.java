package edu.upenn.cit594.util;

import java.util.ArrayList;

public class Population {
    private final String zipCode;

    public Population(String key) {
        zipCode = key;
    }

    // Covid related fields
    public ArrayList<Vaccination> vaccinations;
    // Property data
    public ArrayList<Properties> properties;

    // Population data
    public Integer population;

    public String getZipCode() {
        return zipCode;
    }

    public ArrayList<Vaccination> getDailyVaccinations() {
        return vaccinations;
    }

    public ArrayList<Properties> getPropertyInfos() {
        return properties;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setDailyVaccinations(ArrayList<Vaccination> vaccinations) {
        this.vaccinations = vaccinations;
    }

    public void setPropertyInfos(ArrayList<Properties> properties) {
        this.properties = properties;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }


}
