package lk.ijse.metalworkshopmanagementsystem.model;

public enum Role {
    
    OWNER("Owner"),
    ASSISTANT("Assistant"),
    MASTER_FABRICATOR("Master Fabricator"),
    WELDER("Welder"),
    PAINTER("Painter"),
    WORKSHOP_HELPER("Workshop Helper");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
