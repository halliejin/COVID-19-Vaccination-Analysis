package edu.upenn.cit594.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {

    private String logFile;
    private PrintWriter writer;

    private Logger() {
        setDestination(null);
    }

    private static final Logger instance = new Logger();

    public static Logger getInstance() {
        return instance;
    }

    public void setDestination(String fileName) {
        if (writer != null && this.logFile != null) {
            writer.close();
        }

        this.logFile = fileName;

        if (fileName == null) {
            writer = new PrintWriter(System.err, true);

        } else {
            FileWriter fw;
            try {
                fw = new FileWriter(logFile, true);
            } catch (IOException e) {
                System.out.println("Error: File unable to read");
                throw new RuntimeException(e);
            }

            this.writer = new PrintWriter(fw, true);
        }
    }
    public void log(String message) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        writer.write(timeStamp + " " + message + "\n");
        writer.flush();
    }

    public void close() {
        this.writer.close();
    }
}