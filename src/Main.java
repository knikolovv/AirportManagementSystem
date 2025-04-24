import AirportManagementSystem.AirTrafficControl;
import AirportManagementSystem.Airports.Airport;
import AirportManagementSystem.Airports.CivilAirport;
import AirportManagementSystem.Airports.InternationalAirport;
import AirportManagementSystem.Airports.MilitaryAirport;
import AirportManagementSystem.Flights.Flight;
import AirportManagementSystem.Flights.FlightType;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {

        List<Airport> airportList = List.of(new MilitaryAirport("Military Airport"), new CivilAirport("Civil Airport"), new InternationalAirport("International Airport"));


        ExecutorService executorService = Executors.newCachedThreadPool();
        AirTrafficControl airTrafficControl = new AirTrafficControl(airportList,executorService);

        ScheduledExecutorService generator = Executors.newSingleThreadScheduledExecutor();

        AtomicInteger flightCounter = new AtomicInteger(0);

        generator.scheduleAtFixedRate(() -> {
            if (airTrafficControl.getCompletedFlights() >= 10) {
                generator.shutdown();
                executorService.shutdown();
                return;
            }

            FlightType randomType = FlightType.values()[new Random().nextInt(FlightType.values().length)];
            Airport randomAirport = airportList.get(new Random().nextInt(airportList.size()));

            Flight flight = new Flight(randomType, randomAirport, airTrafficControl);
            executorService.submit(flight);

            flightCounter.incrementAndGet();
        }, 0, 500, TimeUnit.MILLISECONDS);
    }
}