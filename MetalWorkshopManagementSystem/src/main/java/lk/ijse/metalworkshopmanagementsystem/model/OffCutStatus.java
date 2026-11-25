package lk.ijse.metalworkshopmanagementsystem.model;

public enum OffCutStatus {
    
    AVAILABLE("Available"),
    DISCARD("Discard"),
    USED("Used");

    private final String label;

    OffCutStatus(String label) {
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
