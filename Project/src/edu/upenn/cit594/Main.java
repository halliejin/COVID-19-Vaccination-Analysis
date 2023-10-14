package edu.upenn.cit594;

import edu.upenn.cit594.datamanagement.DataReader;
import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.processor.Processor;
import edu.upenn.cit594.ui.UI;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final List<String> ARGS = Arrays.asList("properties", "log", "covid", "population");

    public static void main(String[] args) {
        Map<String, String> arguments;

        try {
            arguments = parseArguments(args);
            validateFiles(arguments);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);

        // set up logger
        Logger logger = Logger.getInstance();
        logger.setDestination(arguments.get("log"));
        logger.log(String.join(" ", args));

        DataReader dataReader = new DataReader(arguments.get("population"), arguments.get("covid"), arguments.get("properties"));
        Processor processor = new Processor(dataReader);

        UI ui = new UI(processor);
        ui.start(scanner);
    }

    private static Map<String, String> parseArguments(String[] args) {
        Map<String, String> arguments = new HashMap<>();

        // match the input pattern
        Pattern argPattern = Pattern.compile("^--(?<name>.+?)=(?<value>.+)$");

        for (String arg : args) {
            Matcher matcher = argPattern.matcher(arg);

            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid argument format.");
            }
            String key = matcher.group("name");
            String value = matcher.group("value");

            if (!ARGS.contains(key)){
                throw new IllegalArgumentException("Unexpected argument name.");
            }

            if (arguments.containsKey(key)) {
                throw new IllegalArgumentException("Duplicate argument detected.");
            }

            if (key.equals("covid") && !(value.toLowerCase().endsWith(".json") || value.toLowerCase().endsWith(".csv"))) {
                throw new IllegalArgumentException("Invalid COVID file type.");
            }

            arguments.put(key, value);
        }

        return arguments;
    }

    private static void validateFiles(Map<String, String> arguments) throws IOException {
        for (String fileArg : ARGS) {
            String thisFile = arguments.get(fileArg);
            if (thisFile != null) {
                File file = new File(thisFile);
                if (!file.exists()) {
                    throw new IOException("File not found.");
                }
            }
        }
    }
}



