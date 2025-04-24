package AirportManagementSystem.Airports;

import AirportManagementSystem.Runways.Runway;
import AirportManagementSystem.Runways.RunwayType;

import java.util.ArrayList;
import java.util.List;

public class CivilAirport extends Airport{
    public CivilAirport(String name) {
        super(name,150);
    }

    @Override
    protected List<Runway> setRunwaysPerAirport() {
        List<Runway> runways = new ArrayList<>();
        runways.add(new Runway(RunwayType.CIVIL));
        runways.add(new Runway(RunwayType.CIVIL));
        runways.add(new Runway(RunwayType.CIVIL));
        runways.add(new Runway(RunwayType.EMERGENCY));
        return runways;
    }
}