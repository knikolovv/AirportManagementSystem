package AirportManagementSystem.Flights;

public enum FlightType {
    CIVIL(3000),
    MILITARY(3000),
    CARGO(4000),
    EMERGENCY(2000);

    private final int processTime;

    FlightType(int processTime) {
        this.processTime = processTime;
    }

    public int getProcessTime() {
        return processTime;
    }
}