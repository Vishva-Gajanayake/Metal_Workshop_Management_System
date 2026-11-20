package lk.ijse.metalworkshopmanagementsystem.model;

public enum UserStatus {
    
    ACTIVE("Active"),
    CLOSED("Closed");

    private final String label;

    UserStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
