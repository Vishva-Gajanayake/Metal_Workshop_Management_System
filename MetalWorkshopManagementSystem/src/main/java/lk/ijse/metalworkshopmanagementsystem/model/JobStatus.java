package lk.ijse.metalworkshopmanagementsystem.model;

public enum JobStatus {
    
    PENDING("Pending"),
    CUTTING("Cutting"),
    FABRICATION("Fabrication"),
    PAINTING("Painting"),
    READY_FOR_DELIVERY("Ready for delivery"),
    INSTALLATION("Installation"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String label;

    JobStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static JobStatus fromDb(String text) {
        try {
            return JobStatus.valueOf(text);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
    
}
