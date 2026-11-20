package lk.ijse.metalworkshopmanagementsystem.model;

public enum MaterialStatus {
    
    OKAY("Okay"),
    LOW("Low");
    
    private final String label;

    MaterialStatus(String label) {
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
