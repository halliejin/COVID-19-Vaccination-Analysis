package edu.upenn.cit594.processor;

import edu.upenn.cit594.util.Properties;
import edu.upenn.cit594.util.Population;

import java.util.ArrayList;

public class Average {
    private Object counter;

    public Average(Object counter) {
        if (!(counter instanceof MarketValue || counter instanceof TotalLivableArea)) {
            throw new IllegalArgumentException("Invalid calculator type.");
        }
        this.counter = counter;
    }

    public Integer calAverage(ArrayList<Population> populations, String zipCode) {
        for (Population thisPopulation : populations) {
            if (thisPopulation.getZipCode().equals(zipCode)) {
                ArrayList<Properties> thisProperties = thisPopulation.getPropertyInfos();
                return calValue(thisProperties);
            }
        }
        return 0;
    }

    private int calValue(ArrayList<Properties> properties) {
        if (counter instanceof MarketValue) {
            return ((MarketValue) counter).avgValue(properties);
        } else if (counter instanceof TotalLivableArea) {
            return ((TotalLivableArea) counter).avgValue(properties);
        }
        return 0;
    }
}