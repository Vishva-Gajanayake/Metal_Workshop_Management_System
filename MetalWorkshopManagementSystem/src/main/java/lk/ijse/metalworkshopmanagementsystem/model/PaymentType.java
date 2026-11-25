package lk.ijse.metalworkshopmanagementsystem.model;

public enum PaymentType {
    ADVANCE("ADVANCE"),
    PARTIAL("PARTIAL"),
    FINAL("FINAL");

    private final String label;

    PaymentType(String label) {
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