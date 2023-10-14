package edu.upenn.cit594.processor;

import edu.upenn.cit594.util.Properties;

import java.util.ArrayList;

public class MarketValue {

    public int avgValue(ArrayList<Properties> properties) {
        // stream
        double totalMktValue = properties.stream()
                .filter(props -> props.getMarketValue() != null)
                .mapToDouble(Properties::getMarketValue)
                .sum();

        long totalProperty = properties.stream()
                .filter(props -> props.getMarketValue() != null)
                .count();

        if (totalProperty == 0) return 0;

        return (int) (totalMktValue / totalProperty);
    }
}
