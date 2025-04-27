package AirportManagementSystem.Logger;

import java.io.IOException;
import java.util.logging.*;

public class FlightLogger {
    private static final Logger logger = Logger.getLogger(FlightLogger.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("flightLog.txt");
            fileHandler.setFormatter(new CustomFileFormatter());

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new CustomConsoleFormatter());
            consoleHandler.setLevel(Level.INFO);

            logger.addHandler(fileHandler);
            logger.addHandler(consoleHandler);

            logger.setUseParentHandlers(false);
            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logActivity(String activity) {
        logger.info(activity);
    }
}
