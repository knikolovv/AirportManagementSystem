package AirportManagementSystem.Airports;

import AirportManagementSystem.Flights.Flight;
import AirportManagementSystem.Runways.Runway;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class Airport {
    private String name;
    private List<Runway> runways;
    private final int maxCapacity;
    private Queue<Flight> flightsQueue;

    public Airport(String name, int maxCapacity) {
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.runways = setRunwaysPerAirport();
        this.flightsQueue = new LinkedList<>();
    }

    public boolean canAcceptFlight(Flight flight) {
        synchronized (flightsQueue) {
            Runway runway = getAvailableCompatibleRunway(flight);
            if (flightsQueue.size() < maxCapacity && runway != null) {
                flightsQueue.offer(flight);
                return true;
            }
            return false;
        }
    }

    public synchronized Runway getAvailableCompatibleRunway(Flight flight) {
        for (Runway runway : runways) {
            if (flight.isCompatibleWithRunway(runway.getRunwayType()) && runway.canUse()) {
                return runway;
            }
        }
        return null;
    }

    protected abstract List<Runway> setRunwaysPerAirport();

    public synchronized void queueFlight(Flight flight) {
        flightsQueue.offer(flight);
    }

    public List<Runway> getRunways() {
        return runways;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public Queue<Flight> getFlightsQueue() {
        return flightsQueue;
    }

    public String getName() {
        return name;
    }
}