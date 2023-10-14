package edu.upenn.cit594.util;

import edu.upenn.cit594.datamanagement.DataReader;

public class Vaccination {
    private String date;
    private int partiallyVaccinated;
    private int fullyVaccinated;

    public int getFullyVaccinated() {
        return fullyVaccinated;
    }

    public void setFullyVaccinated(int fullyVaccinated) {
        this.fullyVaccinated = fullyVaccinated;
    }

    public int getPartiallyVaccinated() {
        return partiallyVaccinated;
    }

    public void setPartiallyVaccinated(int partiallyVaccinated) {
        this.partiallyVaccinated = partiallyVaccinated;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date.length() > 10 ? date.substring(0, 10) : date;
    }
}

