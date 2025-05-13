package AirportManagementSystem;

import AirportManagementSystem.Airports.Airport;
import AirportManagementSystem.Airports.CivilAirport;
import AirportManagementSystem.Airports.InternationalAirport;
import AirportManagementSystem.Airports.MilitaryAirport;
import AirportManagementSystem.Flights.Flight;
import AirportManagementSystem.Flights.FlightType;
import AirportManagementSystem.Logger.FlightLogger;
import AirportManagementSystem.Runways.Runway;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class AirTrafficControl {
    private final List<Airport> airports;
    private final AtomicInteger numberOfTrackedFlights = new AtomicInteger(0);
    private final AtomicInteger flightsLanded = new AtomicInteger(0);
    private final Map<Flight, Future<?>> trackedFlights = new ConcurrentHashMap<>();

    public AirTrafficControl(List<Airport> airports) {
        this.airports = airports;
    }

    public void notifyAirportOfFlightEmergency(Flight flight) {
        FlightLogger.logActivity(flight.getName() + " declared emergency!");
        for (Runway runway : flight.getAirport().getRunways()) {
            if (runway.getLandingFlight() != null && runway.getLandingFlight().getFlightType() != FlightType.EMERGENCY) {
                trackedFlights.get(runway.getLandingFlight()).cancel(true);
            }
        }
    }

    public void processFlight(Flight flight) {
        Airport airport = flight.getAirport();
        if (airport.hasCapacity()) {
            airport.acceptFlight(flight);
        } else {
            FlightLogger.logActivity(airport.getName() + " has no capacity, flight #" + flight.getName() +
                                     " is looking for another airport.");
            transferToAnotherAirport(flight);
        }
    }

    public void transferToAnotherAirport(Flight flight) {
        for (Airport airport : airports) {
            if (!airport.equals(flight.getAirport())) {
                FlightLogger.logActivity("Flight #" + flight.getName() + " is checking for available runways at " + airport.getName());
                if (airport.tryAcceptTransferredFlight(flight)) {
                    flight.setAirport(airport);
                    return;
                }
            }
        }
        FlightLogger.logActivity("Flight #" + flight.getName() + " cannot be redirected to another airport!");
    }

    public Airport pickAirportForFlight(Flight flight) {
        List<Airport> compatibleAirports = switch (flight.getFlightType()) {
            case MILITARY -> airports.stream()
                    .filter(airport -> airport instanceof MilitaryAirport || airport instanceof InternationalAirport)
                    .toList();
            case EMERGENCY -> airports;
            case CARGO, CIVIL -> airports.stream()
                    .filter(airport -> airport instanceof CivilAirport || airport instanceof InternationalAirport)
                    .toList();
        };

        if (compatibleAirports.isEmpty()) {
            throw new RuntimeException("No airport available for flight type: " + flight.getFlightType() + ", flight: " + flight.getName());
        }

        return compatibleAirports.get(new Random().nextInt(compatibleAirports.size()));
    }

    public void registerFlight(Flight flight, Future<?> future) {
        this.trackedFlights.put(flight, future);
    }

    public int getNumberOfTrackedFlights() {
        return numberOfTrackedFlights.get();
    }

    public void incrementTrackedFlights() {
        numberOfTrackedFlights.getAndIncrement();
    }

    public void incrementLandedFlights() {
        flightsLanded.getAndIncrement();
    }

    public int getFlightsLanded() {
        return flightsLanded.get();
    }
}