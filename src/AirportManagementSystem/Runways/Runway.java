package AirportManagementSystem.Runways;

public class Runway {
    private final RunwayType runwayType;
    private final int idNumber;
    private boolean isAvailable = true;

    public Runway(RunwayType runwayType, int idNumber) {
        this.runwayType = runwayType;
        this.idNumber = idNumber;
    }

    public synchronized boolean tryUse() {
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

    @Override
    public String toString() {
        return runwayType + " Runway #" + idNumber;
    }
}
