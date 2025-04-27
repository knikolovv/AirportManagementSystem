package AirportManagementSystem.Airports;

import AirportManagementSystem.Runways.Runway;
import AirportManagementSystem.Runways.RunwayType;

import java.util.ArrayList;
import java.util.List;

public class InternationalAirport extends Airport {

    public InternationalAirport(String name) {
        super(name,100);
    }

    @Override
    protected List<Runway> setRunwaysPerAirport() {
        List<Runway> runways = new ArrayList<>();
        runways.add(new Runway(RunwayType.CIVIL,1));
        runways.add(new Runway(RunwayType.CIVIL,2));
        runways.add(new Runway(RunwayType.EMERGENCY,1));
        runways.add(new Runway(RunwayType.MILITARY,1));
        return runways;
    }
}