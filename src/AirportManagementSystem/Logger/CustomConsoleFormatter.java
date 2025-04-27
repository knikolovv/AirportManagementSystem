package AirportManagementSystem.Logger;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomConsoleFormatter extends Formatter {
    private static final String ANSI_WHITE = "\u001B[97m";
    @Override
    public String format(LogRecord record) {
        return ANSI_WHITE + record.getMessage() + System.lineSeparator();
    }
}
