package AirportManagementSystem.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomFileFormatter extends Formatter {
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public String format(LogRecord record) {
        LocalDateTime activityTimestamp = LocalDateTime.now();
        return record.getMessage() + " " + activityTimestamp.format(dateFormat) + System.lineSeparator();
    }
}
