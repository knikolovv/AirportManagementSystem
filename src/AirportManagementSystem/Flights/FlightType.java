package AirportManagementSystem.Flights;

public enum FlightType {
    CIVIL(3),
    MILITARY(3),
    CARGO(4),
    EMERGENCY(2);

    private int processTime;

    FlightType(int processTime) {
        this.processTime = processTime;
    }

    public int getProcessTime() {
        return processTime;
    }
}