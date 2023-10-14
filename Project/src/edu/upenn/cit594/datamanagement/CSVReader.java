package edu.upenn.cit594.datamanagement;

import edu.upenn.cit594.logging.Logger;

import java.io.IOException;
import java.util.*;

public class CSVReader {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 5130409650040L;
    private final CharacterReader reader;
    private boolean reachedEOF = false;

    public CSVReader(CharacterReader reader) {
        this.reader = reader;
    }

    /**
     * This method uses the class's {@code CharacterReader} to read in just enough
     * characters to process a single valid CSV row, represented as an array of
     * strings where each element of the array is a field of the row. If formatting
     * errors are encountered during reading, this method throws a
     * {@code CSVFormatException} that specifies the exact point at which the error
     * occurred.
     *
     * @return a single row of CSV represented as a string array, where each
     * element of the array is a field of the row; or {@code null} when
     * there are no more rows left to be read.
     * @throws IOException        when the underlying reader encountered an error
     * @throws CSVFormatException when the CSV file is formatted incorrectly
     */
    public String[] readRow() throws IOException, CSVFormatException {
        if (reachedEOF) return null;

        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        int line = 1, column = 0, row = 1, field = 1;

        final int ROW_START = 0, FIELD_START = 1, ESCAPED = 2, UNESCAPED = 3, CR = 4, QUOTE_ESCAPE = 5;
        int state = ROW_START;

        while (true) {
            int c = reader.read();
            column++;

            if (c == -1) {
                reachedEOF = true;

                if (state == ESCAPED) {
                    throw new CSVFormatException("Unterminated quoted field at end of file", line, column, row, field);
                }

                if (state == CR) {
                    throw new CSVFormatException("Carriage return at end of file", line, column, row, field);
                }

                if (state == QUOTE_ESCAPE) {
                    fields.add(sb.toString());
                    sb.setLength(0);
                }

                if (sb.length() > 0 || state == FIELD_START) {
                    fields.add(sb.toString());
                }
                break;
            }

            char ch = (char) c;
            switch (state) {
                case ROW_START:
                case FIELD_START:
                    if (ch == ',') {
                        fields.add(sb.toString());
                        sb.setLength(0);
                        field++;
                        state = FIELD_START;
                    } else if (ch == '\n') {
                        fields.add(sb.toString());
                        sb.setLength(0);
                        row++;
                        column = 0;
                        field = 1;
                        return fields.toArray(new String[0]);
                    } else if (ch == '\r') {
                        state = CR;
                    } else if (ch == '"') {
                        state = ESCAPED;
                    } else {
                        sb.append(ch);
                        state = UNESCAPED;
                    }
                    break;
                case UNESCAPED:
                    if (ch == ',') {
                        fields.add(sb.toString());
                        sb.setLength(0);
                        field++;
                        state = FIELD_START;
                    } else if (ch == '\n') {
                        fields.add(sb.toString());
                        sb.setLength(0);
                        row++;
                        column = 0;
                        field = 1;
                        return fields.toArray(new String[0]);
                    } else if (ch == '\r') {
                        state = CR;
                    } else if (ch == '"') {
                        throw new CSVFormatException("Quotes are not acceptable in unescaped fields.", line, column, row, field);
                    } else {
                        sb.append(ch);
                    }
                    break;
                case ESCAPED:
                    if (ch == '"') {
                        state = QUOTE_ESCAPE;
                    } else {
                        sb.append(ch);
                    }
                    break;
                case QUOTE_ESCAPE:
                    if (ch == '"') {
                        sb.append(ch);
                        state = ESCAPED;
                    } else if (ch == ',') {
                        fields.add(sb.toString());
                        sb.setLength(0);
                        field++;
                        state = FIELD_START;
                    } else if (ch == '\n') {
                        fields.add(sb.toString());
                        sb.setLength(0);
                        row++;
                        column = 0;
                        field = 1;
                        return fields.toArray(new String[0]);
                    } else if (ch == '\r') {
                        state = CR;
                    } else {
                        throw new CSVFormatException("Unexpected character after closing quote", line, column, row, field);
                    }
                    break;
                case CR:
                    if (ch == '\n') {
                        fields.add(sb.toString());
                        sb.setLength(0);
                        row++;
                        column = 0;
                        field = 1;
                        return fields.toArray(new String[0]);
                    } else {
                        throw new CSVFormatException("Carriage return must be followed by newline", line, column, row, field);
                    }
            }
        }

        return fields.isEmpty() ? null : fields.toArray(new String[0]);
    }
}
