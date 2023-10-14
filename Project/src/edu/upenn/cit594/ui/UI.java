package edu.upenn.cit594.ui;

import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.processor.Processor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.IntStream;

public class UI {
    protected Processor processor;
    protected ArrayList<String> availableFunctions;
    protected ArrayList<String> unAvailableFunctions;
    Logger logger;

    public UI(Processor processor) {
        this.processor = processor;
        availableFunctions = new ArrayList<>();
        unAvailableFunctions = new ArrayList<>();
        setAvailableFunctions();

        logger = Logger.getInstance();
    }


    private void setAvailableFunctions() {
        boolean[] availableFunctionsArray = processor.getIndicators();
        IntStream.range(0, availableFunctionsArray.length).forEach(i -> {
            if (availableFunctionsArray[i]) {
                availableFunctions.add(String.valueOf(i));
            } else {
                unAvailableFunctions.add(String.valueOf(i));
            }
        });
    }


    private String getOptionInput(Scanner scanner) {
        String input;
        while (true) {
            System.out.println("Please enter your option: ");
            System.out.print("> ");
            System.out.flush();
            input = scanner.nextLine();
            logger.log(input);

            if (availableFunctions.contains(input)) {
                break;
            } else if (unAvailableFunctions.contains(input)) {
                System.out.println("Function not available. Please try again:");
            } else {
                System.out.println("Invalid input. Please try again:");
            }
        }

        return input;
    }

    private String getDateInput(Scanner scanner) {
        String input;
        while (true) {
            System.out.println("Please enter the date in the format of YYYY-MM-DD: ");
            System.out.print("> ");
            System.out.flush();
            input = scanner.nextLine();
            logger.log(input);

            if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
                break;
            } else {
                System.out.println("Invalid input. Please try again:");
                //askForDateInput(scanner);
            }
        }

        return input;
    }

    private String getZipCodeInput(Scanner scanner) {
        String input;
        while (true) {
            System.out.println("Please enter the zip code: ");
            System.out.print("> ");
            System.out.flush();
            input = scanner.nextLine();
            logger.log(input);

            if (input.matches("\\d{5}")) {
                break;
            } else {
                System.out.println("Invalid input. Please try again:");
            }
        }

        return input;
    }

    private String getFOrPResp(Scanner scanner) {
        String input;
        while (true) {
            System.out.println("Please enter <full> or <partial>: ");
            System.out.print("> ");
            System.out.flush();
            input = scanner.nextLine();
            logger.log(input);

            if (input.equals("full") || input.equals("partial")) {
                break;
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }

        return input;
    }


    private void printOutputHeader() {
        System.out.println();
        System.out.println("BEGIN OUTPUT");
    }

    private void printOutputFooter() {
        System.out.println("END OUTPUT");
        System.out.println();
    }

    private void printAllPopulation() {
        printOutputHeader();
        System.out.println(processor.calTotalPopulations());
        printOutputFooter();
    }

    private void printAvaiFuncts() {
        printOutputHeader();
        for (String thisString : availableFunctions) System.out.println(thisString);
        printOutputFooter();
    }

    private void printVaccPerCap(String date, String type) {
        printOutputHeader();
        Map<String, Double> vaccination = processor.calVaccinationPerCap(date, type);
        DecimalFormat decimalFormat = new DecimalFormat("#0.0000");

        if (vaccination.size() == 0) System.out.println("0");

        for (Map.Entry<String, Double> e : vaccination.entrySet()) {
            String formattedNumber = decimalFormat.format(e.getValue());
            System.out.println(e.getKey() + " " + formattedNumber);
        }

        printOutputFooter();
    }

    private void printAvgMktValuePerCap(String zipCode) {
        printOutputHeader();
        System.out.println(processor.calTotalMktValuePerCap(zipCode));
        printOutputFooter();
    }

    private void printAvgMktValue(String zipCode) {
        printOutputHeader();
        System.out.println(processor.calAvgMktValue(zipCode));
        printOutputFooter();
    }

    private void printAvgTotalLivableArea(String zipCode) {
        printOutputHeader();
        System.out.println(processor.calAvgLivableArea(zipCode));
        printOutputFooter();
    }

    private void printTotalLivableAreaPerCap(String zipCode) {
        printOutputHeader();
        System.out.println(processor.calTotalLivableAreaPerCap(zipCode));
        printOutputFooter();
    }

    private void printMainMenu() {
        System.out.println("0. Exit the program");
        System.out.println("1. Show the available actions");
        System.out.println("2. Show the total population for all ZIP Codes");
        System.out.println("3. Show the total vaccinations per capita for each ZIP Code for the specified date");
        System.out.println("4. Show the average market value for properties in a specified ZIP Code");
        System.out.println("5. Show the average total livable area for properties in a specified ZIP Code");
        System.out.println("6. Show the total market value of properties, per capita, for a specified ZIP Code");
        System.out.println("7. Show the total livable area for properties, per capita, for a specified ZIP Code");
    }

    public void start(Scanner scanner) {
        while (true) {
            printMainMenu();
            // get user input
            String input = getOptionInput(scanner);

            if ("0".equals(input)) {
                // exit the program
                break;
            }

            switch (input) {
                case "1":
                    printAvaiFuncts();
                    break;
                case "2":
                    printAllPopulation();
                    break;
                case "3":
                    String type = getFOrPResp(scanner);
                    String date = getDateInput(scanner);
                    printVaccPerCap(date, type);
                    break;
                case "4":
                case "5":
                case "6":
                case "7":
                    handleZipCode(input, scanner);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        logger.close();
        scanner.close();
    }

    private void handleZipCode(String input, Scanner scanner) {
        String zipCode = getZipCodeInput(scanner);
        switch (input) {
            case "4":
                printAvgMktValue(zipCode);
                break;
            case "5":
                printAvgTotalLivableArea(zipCode);
                break;
            case "6":
                printAvgMktValuePerCap(zipCode);
                break;
            case "7":
                printTotalLivableAreaPerCap(zipCode);
                break;
        }
    }

}
