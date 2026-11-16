package lk.ijse.metalworkshopmanagementsystem.model;

public enum UnitOfMeasure {
    
    PER_M2("per m²"),
    PER_M("per m"),
    PER_STEP("per step"),
    PER_PIECE("per piece"),
    PER_SET("per set"),
    PER_HOUR("per hour");

    private final String label;

    UnitOfMeasure(String label) {
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
