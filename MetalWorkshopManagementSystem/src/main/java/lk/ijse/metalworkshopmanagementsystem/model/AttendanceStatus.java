package lk.ijse.metalworkshopmanagementsystem.model;

public enum AttendanceStatus {
    
    PRESENT("Present"),
    LEAVE("Leave"),
    HALF_DAY("Half Day"),
    ABSENT("Absent");

    private final String label;

    AttendanceStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
