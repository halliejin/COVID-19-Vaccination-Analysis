package edu.upenn.cit594.processor;

import edu.upenn.cit594.datamanagement.DataReader;
import edu.upenn.cit594.util.Vaccination;
import edu.upenn.cit594.util.Properties;
import edu.upenn.cit594.util.Population;

import java.util.*;


public class Processor {
    private boolean[] indicators;
    private Integer totalPop;
    private final ArrayList<Population> popList;
    private final Map<String, Map<String, Map<String, Double>>> vaccPerCapMap;
    private final Map<String, Integer> mktValPerCapMap;
    private final Map<String, Integer> livableAreaPerCapMap;
    private final Map<String, Integer> avgMktValueMap;
    private final Map<String, Integer> avgLivableAreaMap;
    public Processor(DataReader dataReader) {
        popList = dataReader.getAllZipCodes();
        setIndicators(dataReader);
        vaccPerCapMap = new HashMap<>();
        mktValPerCapMap = new HashMap<>();
        livableAreaPerCapMap = new HashMap<>();
        avgMktValueMap =  new HashMap<>();
        avgLivableAreaMap = new HashMap<>();
    }

    public void setIndicators(DataReader dataReader) {
        boolean hasPopulationData = dataReader.getHasPopulationData();
        boolean hasVaccinationData = dataReader.getHasVaccinationData() && hasPopulationData;
        boolean hasPropertyData = dataReader.getHasPropertyData();

        indicators = new boolean[] {
            true, true, hasPopulationData, hasVaccinationData,
            hasPropertyData, hasPropertyData,
            hasPropertyData && hasPopulationData,
            hasPropertyData && hasPopulationData
        };
    }

    public boolean[] getIndicators() {
        return indicators;
    }

    public int calTotalPopulations() {
        if (totalPop == null) {
            totalPop = 0;
            for (Population population : popList) {
                Integer population1 = population.getPopulation();
                if (population1 != null) {
                    totalPop += population1;
                }
            }
        }

        return totalPop;
    }

    // 3.3
    public Map<String, Double> calVaccinationPerCap(String specificDate, String vaccinationType) {
        Map<String, Map<String, Double>> dateToVaccinationMap = vaccPerCapMap.get(specificDate);
        if (dateToVaccinationMap != null && dateToVaccinationMap.containsKey(vaccinationType)) {
            return dateToVaccinationMap.get(vaccinationType);
        }

        Map<String, Double> vaccinationPerCapitaMap = new TreeMap<>();

        for (Population populationEntry : popList) {
            String zipCode = populationEntry.getZipCode();
            Integer populationSize = populationEntry.getPopulation();
            ArrayList<Vaccination> dailyVaccinations = populationEntry.getDailyVaccinations();

            if (populationSize != null && dailyVaccinations != null) {
                for (Vaccination vaccinationEntry : dailyVaccinations) {
                    if (vaccinationEntry.getDate().contains(specificDate)) {
                        double vaccinationCount = vaccinationType.equals("partial") ?
                                vaccinationEntry.getPartiallyVaccinated() : vaccinationEntry.getFullyVaccinated();

                        double perCapVaccRate = vaccinationCount / populationSize;

                        vaccinationPerCapitaMap.put(zipCode, perCapVaccRate);
                    }
                }
            }
        }

        if (dateToVaccinationMap == null) {
            dateToVaccinationMap = new HashMap<>();
            vaccPerCapMap.put(specificDate, dateToVaccinationMap);
        }
        dateToVaccinationMap.put(vaccinationType, vaccinationPerCapitaMap);

        return vaccinationPerCapitaMap;
    }


    // 3.4
    public Integer calAvgMktValue(String zipCode) {
        if (avgMktValueMap.containsKey(zipCode)) {
            return avgMktValueMap.get(zipCode);
        }

        MarketValue marketValue = new MarketValue();
        Average avg = new Average(marketValue);
        Integer result = avg.calAverage(this.popList, zipCode);

        avgMktValueMap.put(zipCode, result);

        return result;
    }

    // 3.5
    public Integer calAvgLivableArea(String zipCode) {
        if (avgLivableAreaMap.containsKey(zipCode)) {
            return avgLivableAreaMap.get(zipCode);
        }

        TotalLivableArea totalLivableArea = new TotalLivableArea();
        Average avg = new Average(totalLivableArea);
        Integer result = avg.calAverage(this.popList, zipCode);

        avgLivableAreaMap.put(zipCode, result);

        return result;
    }

    // 3.6
    public int calTotalMktValuePerCap(String zipCode) {
        if (mktValPerCapMap.containsKey(zipCode)) {
            return mktValPerCapMap.get(zipCode);
        }

        Population population = findPopulationByZipCode(zipCode);
        if (population == null || population.getPopulation() == null || population.getPopulation() == 0) {
            mktValPerCapMap.put(zipCode, 0);
            return 0;
        }

        double totalMarketValue = calTotalMktVal(population.getPropertyInfos());
        int marketValuePerCap = (int) (totalMarketValue / population.getPopulation());

        mktValPerCapMap.put(zipCode, marketValuePerCap);
        return marketValuePerCap;
    }

    private Population findPopulationByZipCode(String zipCode) {
        for (Population population : popList) {
            if (population.getZipCode().equals(zipCode)) {
                return population;
            }
        }
        return null;
    }

    private double calTotalMktVal(List<Properties> properties) {
        return properties.stream()
                .map(Properties::getMarketValue)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    // 3.7
    public Integer calTotalLivableAreaPerCap(String zipCode) {
        if (livableAreaPerCapMap.containsKey(zipCode)) {
            return livableAreaPerCapMap.get(zipCode);
        }

        double totalLivableArea = 0.0;
        double population = 0.0;

        for (Population i : popList) {
            if (i.getZipCode().equals(zipCode)) {
                Integer pop = i.getPopulation();
                if (pop != null) {
                    population = (double) pop;
                }

                totalLivableArea += calTotalLivableArea(i.getPropertyInfos());
            }
        }

        if (population == 0) {
            livableAreaPerCapMap.put(zipCode, 0);
            return 0;
        }

        int totalLivableAreaPerCap = (int) (totalLivableArea / population);
        livableAreaPerCapMap.put(zipCode, totalLivableAreaPerCap);
        return totalLivableAreaPerCap;
    }

    private double calTotalLivableArea(List<Properties> properties) {
        return properties.stream()
                .map(Properties::getTotalLivableArea)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
    }


}
