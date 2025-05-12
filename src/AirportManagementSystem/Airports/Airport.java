package AirportManagementSystem.Airports;

import AirportManagementSystem.Flights.Flight;
import AirportManagementSystem.Flights.FlightType;
import AirportManagementSystem.Logger.FlightLogger;
import AirportManagementSystem.Runways.Runway;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public abstract class Airport {
    private final String name;
    private final List<Runway> runways;
    private final int maxCapacity;
    private final Deque<Flight> flightsQueue;
    private final ReentrantReadWriteLock emergencyLock = new ReentrantReadWriteLock(true);
    private final ReentrantLock acceptLock = new ReentrantLock();

    public Airport(String name, int maxCapacity) {
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.runways = setRunwaysPerAirport();
        this.flightsQueue = new ConcurrentLinkedDeque<>();
    }

    public boolean hasCapacity() {
        return flightsQueue.size() < maxCapacity;
    }

    public boolean tryAcceptTransferredFlight(Flight flight) {
        acceptLock.lock();
        try {
            if (flight.isCompatibleWithAirport(this) && flightsQueue.size() < maxCapacity) {
                acceptFlight(flight);
                return true;
            } else {
                FlightLogger.logActivity("Airport " + getName() + " is full cannot accept flight #" + flight.getName());
                return false;
            }
        } finally {
            acceptLock.unlock();
        }
    }

    public void acceptFlight(Flight flight) {
        FlightLogger.logActivity("Flight #" + flight.getName() + " queued at " + this.name);
        if (flight.getFlightType() == FlightType.EMERGENCY) {
            if (emergencyLock.isWriteLocked()) {
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

    public Deque<Flight> getFlightsQueue() {
        return flightsQueue;
    }

    public ReentrantReadWriteLock getEmergencyLock() {
        return emergencyLock;
    }
}