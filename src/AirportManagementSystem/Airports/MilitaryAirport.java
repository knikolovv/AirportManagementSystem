package AirportManagementSystem.Airports;

import AirportManagementSystem.Runways.Runway;
import AirportManagementSystem.Runways.RunwayType;

import java.util.ArrayList;
import java.util.List;

public class MilitaryAirport extends Airport{

    public MilitaryAirport(String name) {
        super(name,50);
    }

    @Override
    protected List<Runway> setRunwaysPerAirport() {
        List<Runway> runways = new ArrayList<>();
        runways.add(new Runway(RunwayType.MILITARY,1));
        runways.add(new Runway(RunwayType.MILITARY,2));
        runways.add(new Runway(RunwayType.EMERGENCY,1));
        return runways;
    }
}