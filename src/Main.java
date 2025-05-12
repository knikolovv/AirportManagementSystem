import AirportManagementSystem.AirTrafficControl;
import AirportManagementSystem.Airports.Airport;
import AirportManagementSystem.Airports.CivilAirport;
import AirportManagementSystem.Airports.InternationalAirport;
import AirportManagementSystem.Airports.MilitaryAirport;
import AirportManagementSystem.Flights.Flight;
import AirportManagementSystem.Flights.FlightType;
import AirportManagementSystem.Logger.FlightLogger;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {

    private static final int FLIGHTS_COUNT = 100;
    private static final Random random = new Random();

    public static void main(String[] args) {
        List<Airport> airportList = List.of(new MilitaryAirport("Ramstein AB"), new CivilAirport("Sofia ICAO"), new InternationalAirport("Frankfurt IATA"));
        AirTrafficControl airTrafficControl = new AirTrafficControl(airportList);

        ExecutorService flightExecutor = Executors.newCachedThreadPool();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            if (airTrafficControl.getNumberOfTrackedFlights() < FLIGHTS_COUNT) {
                FlightType[] flightTypes = {FlightType.CIVIL, FlightType.CARGO, FlightType.MILITARY};
                FlightType randomType = flightTypes[random.nextInt(flightTypes.length)];
                Flight flight = new Flight(randomType, airTrafficControl);
                airTrafficControl.registerFlight(flight, flightExecutor.submit(flight));
            } else {
                FlightLogger.logActivity(airTrafficControl.getNumberOfTrackedFlights() + " flights have been created!");
                flightExecutor.shutdown();
                scheduler.shutdownNow();
            }
        }, 0, 500, TimeUnit.MILLISECONDS);

        try {
            flightExecutor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            FlightLogger.logActivity("There was an error while waiting for the flights!");
        }

        FlightLogger.logActivity("All " + airTrafficControl.getFlightsLanded() + " flights have landed.");
        FlightLogger.logActivity("End of simulation");
    }
}