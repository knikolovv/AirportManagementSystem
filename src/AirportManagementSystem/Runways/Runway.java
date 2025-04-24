package AirportManagementSystem.Runways;

public class Runway {
    private final RunwayType runwayType;
    private boolean isAvailable = true;

    public Runway(RunwayType runwayType) {
        this.runwayType = runwayType;
    }

    public synchronized boolean canUse() {
        if (isAvailable) {
            isAvailable = false;
            return true;
        }
        return false;
    }

    public void free() {
        this.isAvailable = true;
    }

    public RunwayType getRunwayType() {
        return runwayType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
