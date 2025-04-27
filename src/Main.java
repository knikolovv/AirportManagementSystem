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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    private final static int FLIGHTS_COUNT = 100;

    public static void main(String[] args) {
        List<Airport> airportList = List.of(new MilitaryAirport("Ramstein AB"), new CivilAirport("Sofia ICAO"), new InternationalAirport("Frankfurt IATA"));
        AirTrafficControl airTrafficControl = new AirTrafficControl(airportList);

        ExecutorService flightExecutor = Executors.newCachedThreadPool();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            if (airTrafficControl.getTrackedFlights().get() < FLIGHTS_COUNT) {
                FlightType[] flightTypes = {FlightType.CIVIL, FlightType.CARGO, FlightType.MILITARY};
                FlightType randomType = flightTypes[new Random().nextInt(flightTypes.length)];
                Flight flight = new Flight(randomType, airTrafficControl);
                flightExecutor.submit(flight);
            } else {
                FlightLogger.logActivity(airTrafficControl.getTrackedFlights() + " flights have been created!");
                flightExecutor.shutdown();
                scheduler.shutdownNow();
            }
        }, 0, 500, TimeUnit.MILLISECONDS);

        try {
            flightExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        FlightLogger.logActivity("All " + airTrafficControl.getFlightsLanded() + " flights have landed.");
        FlightLogger.logActivity("End of simulation");
    }
}