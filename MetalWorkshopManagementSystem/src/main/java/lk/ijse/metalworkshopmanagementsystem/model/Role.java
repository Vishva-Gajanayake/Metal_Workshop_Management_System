package lk.ijse.metalworkshopmanagementsystem.model;

public enum Role {
    
    OWNER("Owner"),
    MANAGER("Manager"),
    WORKER("Helper");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
