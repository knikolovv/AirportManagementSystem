package AirportManagementSystem.Runways;

import AirportManagementSystem.Flights.Flight;

public class Runway {
    private final RunwayType runwayType;
    private final int idNumber;
    private Flight landingFlight;

    public Runway(RunwayType runwayType, int idNumber) {
        this.runwayType = runwayType;
        this.idNumber = idNumber;
    }

    public synchronized boolean tryLand(Flight flight) {
        if (landingFlight == null) {
            landingFlight = flight;
            return true;
        }
        return false;
    }

    public void free() {
        landingFlight = null;
    }

    public RunwayType getRunwayType() {
        return runwayType;
    }

    public boolean isAvailable() {
        return landingFlight == null;
    }

    public Flight getLandingFlight() {
        return landingFlight;
    }

    @Override
    public String toString() {
        return runwayType + " Runway #" + idNumber;
    }
}
