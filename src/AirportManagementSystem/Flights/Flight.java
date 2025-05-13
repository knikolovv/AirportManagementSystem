package AirportManagementSystem.Flights;

import AirportManagementSystem.AirTrafficControl;
import AirportManagementSystem.Airports.Airport;
import AirportManagementSystem.Logger.FlightLogger;
import AirportManagementSystem.Runways.Runway;
import AirportManagementSystem.Runways.RunwayType;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;


public class Flight implements Runnable {
    private final String name;
    private FlightType flightType;
    private Airport airport;
    private final AirTrafficControl airTrafficControl;
    private boolean hasLanded;
    private static final Random random = new Random();

    public Flight(FlightType flightType, AirTrafficControl airTrafficControl) {
        this.flightType = flightType;
        this.airport = airTrafficControl.pickAirportForFlight(this);
        this.name = airport.getName().substring(0, 3) + random.nextInt(1, 500);
        this.airTrafficControl = airTrafficControl;
    }

    @Override
    public void run() {
        FlightLogger.logActivity(this.flightType + " Flight #" + this.name + " took off" + " and is looking for a free runway at " + this.airport.getName());

        if (random.nextInt(0, 101) < 5) {
            this.setFlightType(FlightType.EMERGENCY);
            this.airTrafficControl.notifyAirportOfFlightEmergency(this);
        }
        this.airTrafficControl.processFlight(this);

        while (!hasLanded) {
            try {
                if (tryToLand()) {
                    landSuccessfully();
                    break;
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Current landing interrupted, continue attempting to land
            }
        }
    }

    private void landSuccessfully() {
        hasLanded = true;
        airTrafficControl.incrementLandedFlights();
    }

    public boolean tryToLand() {
        if (this.flightType != FlightType.EMERGENCY && this.airport.getFlightsQueue().peek() != this) {
            return false;
        }

        Runway runway = waitForCompatibleRunway();
        if (runway == null) {
            return false;
        }

        airport.getFlightsQueue().remove(this);
        if (this.flightType == FlightType.EMERGENCY) {
            this.airport.getEmergencyLock().writeLock().lock();
            FlightLogger.logActivity("Emergency Flight #" + this.name + " is landing with priority!");
        } else {
            this.airport.getEmergencyLock().readLock().lock();
        }

        try {
            FlightLogger.logActivity("Flight with #" + this.name + " is using " + runway + " on " + this.airport.getName() + ".");
            Thread.sleep(this.flightType.getProcessTime());
            FlightLogger.logActivity("Flight #" + this.name + " landed successfully on " + runway + " and freed it.");
        } catch (InterruptedException e) {
            this.airport.getFlightsQueue().offerFirst(this);
            FlightLogger.logActivity("The landing of flight #" + this.name + " was interrupted during emergency!");
            return false;
        } finally {
            runway.free();
            if (this.flightType == FlightType.EMERGENCY) {
                this.airport.getEmergencyLock().writeLock().unlock();
            } else {
                this.airport.getEmergencyLock().readLock().unlock();
            }
        }
        return true;
    }

    private Runway waitForCompatibleRunway() {
        Runway runway = getCompatibleRunway();
        if (runway != null && runway.tryLand(this)) {
            return runway;
        }
        return null;
    }

    public Runway getCompatibleRunway() {
        return this.airport.getRunways().stream()
                .filter(runway -> isCompatibleWithRunwayType(runway.getRunwayType()) && runway.isAvailable())
                .findFirst()
                .orElse(null);
    }

    public boolean isCompatibleWithRunwayType(RunwayType type) {
        return switch (this.flightType) {
            case CARGO, CIVIL -> type == RunwayType.CIVIL;
            case MILITARY -> type == RunwayType.MILITARY;
            case EMERGENCY -> type == RunwayType.EMERGENCY;
        };
    }

    public boolean isCompatibleWithAirport(Airport airport) {
        boolean compatible = airport.getRunways().stream()
                .anyMatch(runway -> isCompatibleWithRunwayType(runway.getRunwayType()));

        if (!compatible) {
            FlightLogger.logActivity("Flight #" + this.name + " is not compatible with " +
                                     airport.getName() + "'s runways");
        }
        return compatible;
    }

    public String getName() {
        return name;
    }

    public FlightType getFlightType() {
        return flightType;
    }

    public void setFlightType(FlightType type) {
        this.flightType = type;
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