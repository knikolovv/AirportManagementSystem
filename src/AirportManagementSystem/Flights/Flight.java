package AirportManagementSystem.Flights;

import AirportManagementSystem.AirTrafficControl;
import AirportManagementSystem.Airports.Airport;
import AirportManagementSystem.Runways.Runway;
import AirportManagementSystem.Runways.RunwayType;

import java.util.List;
import java.util.UUID;


public class Flight implements Runnable {
    private UUID id;
    private FlightType flightType;
    private Airport airport;
    private AirTrafficControl airTrafficControl;

    public Flight(FlightType flightType, Airport airport, AirTrafficControl airTrafficControl) {
        this.id = UUID.randomUUID();
        this.flightType = flightType;
        this.airport = airport;
        this.airTrafficControl = airTrafficControl;
    }

    @Override
    public void run() {
        System.out.println("Flight #" + this.id + " type " +  this.flightType +  " is looking for free runway at " + this.airport.getName());
        Runway runway = airport.getAvailableCompatibleRunway(this);
        if (runway == null) {
            boolean accepted = airport.canAcceptFlight(this);
            if (!accepted) {
                System.out.println("Flight #" + this.id + " is being transferred to another airport");
                Airport newAirport = airTrafficControl.transferToAnotherAirport(this);
                if (newAirport != null) {
                    this.setAirport(newAirport);
                    airTrafficControl.getExecutorService().submit(this);
                }
                return;
            }
            else {
                System.out.println("Flight " + this.id + " queued at " + this.airport.getName());

            }
            return;
        }

        if (runway.canUse()) {
            System.out.println("Flight with id " + this.id +" is using runway " + runway.getRunwayType() + " on " + airport.getName());
            try {
                Thread.sleep(this.flightType.getProcessTime());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            System.out.println("Flight #" + this.id + " landed successfully and freed the runway");
            runway.free();
            airTrafficControl.completeFlight();
        } else {
            airport.queueFlight(this);
        }
    }

    public boolean isCompatibleWithRunway(RunwayType type) {
        return switch (this.flightType) {
            case CARGO -> type == RunwayType.CIVIL;
            case MILITARY -> type == RunwayType.MILITARY;
            case CIVIL -> type == RunwayType.CIVIL;
            case EMERGENCY -> type == RunwayType.EMERGENCY;
        };
    }
    public boolean isCompatibleWithAnotherAirportRunway(List<Runway> runways) {
        for (Runway runway : runways) {
            if (isCompatibleWithRunway(runway.getRunwayType())) {
                return true;
            }
        }
        System.out.println("The flight is not compatible with any runway");
        return false;
    }

    public UUID getId() {
        return id;
    }

    public FlightType getFlightType() {
        return flightType;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }

    public AirTrafficControl getAirportSystem() {
        return airTrafficControl;
    }
}