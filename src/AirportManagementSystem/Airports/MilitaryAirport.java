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
        runways.add(new Runway(RunwayType.MILITARY));
        runways.add(new Runway(RunwayType.MILITARY));
        runways.add(new Runway(RunwayType.EMERGENCY));
        return runways;
    }
}