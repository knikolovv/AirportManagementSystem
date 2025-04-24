package AirportManagementSystem;

import AirportManagementSystem.Airports.Airport;
import AirportManagementSystem.Flights.Flight;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class AirTrafficControl {
    private List<Airport> airports;
    private int completedFlights = 0;
    private ExecutorService executorService;


    public AirTrafficControl(List<Airport> airports, ExecutorService executorService) {
        this.airports = airports;
        this.executorService = executorService;
    }

    public Airport transferToAnotherAirport(Flight flight) {
        for (Airport airport : airports) {
            if (!airport.equals(flight.getAirport())) {
                if (flight.isCompatibleWithAnotherAirportRunway(airport.getRunways()) &&
                    airport.canAcceptFlight(flight)) {
                    flight.setAirport(airport);
                    System.out.println("Changed flight #" + flight.getId() + " to airport " + airport.getName());
                    executorService.submit(flight);
                    return airport;
                }
            }
        }
        System.out.println("The flight cannot be redirected to another airport!");
        return null;
    }

    public int getCompletedFlights() {
        return completedFlights;
    }

    public synchronized void completeFlight() {
        completedFlights++;
    }

    public List<Airport> getAirports() {
        return airports;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}