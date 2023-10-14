package edu.upenn.cit594.processor;

import edu.upenn.cit594.util.Properties;

import java.util.ArrayList;

public class TotalLivableArea {

    public int avgValue(ArrayList<Properties> properties) {
        // stream
        double totalLivableArea = properties.stream()
                .filter(props -> props.getTotalLivableArea() != null)
                .mapToDouble(Properties::getTotalLivableArea)
                .sum();

        long totalProperty = properties.stream()
                .filter(props -> props.getTotalLivableArea() != null)
                .count();


        if (totalProperty == 0) return 0;

        return (int) (totalLivableArea/ totalProperty);
    }
}
