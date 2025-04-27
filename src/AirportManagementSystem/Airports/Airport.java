package AirportManagementSystem.Airports;

import AirportManagementSystem.Flights.Flight;
import AirportManagementSystem.Flights.FlightType;
import AirportManagementSystem.Logger.FlightLogger;
import AirportManagementSystem.Runways.Runway;

import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public abstract class Airport {
    private final String name;
    private final List<Runway> runways;
    private final int maxCapacity;
    private final Queue<Flight> flightsQueue;
    private final ReentrantReadWriteLock emergencyLock = new ReentrantReadWriteLock(true);

    public Airport(String name, int maxCapacity) {
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.runways = setRunwaysPerAirport();
        this.flightsQueue = new ConcurrentLinkedQueue<>();
    }

    public boolean hasCapacity() {
        return flightsQueue.size() < maxCapacity;
    }

    public void acceptFlight(Flight flight) {
        FlightLogger.logActivity("Flight #" + flight.getName() + " queued at " + this.name);
        if (new Random().nextInt(0, 101) < 5) {
            flight.setFlightType(FlightType.EMERGENCY);
            FlightLogger.logActivity(flight.getName() + " declared emergency!");
        }
        if (flight.getFlightType() == FlightType.EMERGENCY) {
            if (emergencyLock.isWriteLocked() || getFlightsQueue().stream().anyMatch(fl -> fl.getFlightType() == FlightType.EMERGENCY)) {
                FlightLogger.logActivity(this.name + " is currently handling emergency landing!");
                flight.getAirportSystem().transferToAnotherAirport(flight);
                return;
            }
        }
        flightsQueue.offer(flight);
        flight.getAirportSystem().incrementTrackedFlights();
    }

    protected abstract List<Runway> setRunwaysPerAirport();

    public List<Runway> getRunways() {
        return runways;
    }

    public String getName() {
        return name;
    }

    public Queue<Flight> getFlightsQueue() {
        return flightsQueue;
    }

    public ReentrantReadWriteLock getEmergencyLock() {
        return emergencyLock;
    }
}