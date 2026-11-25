package lk.ijse.metalworkshopmanagementsystem.model;

public enum PaymentMethod {
    CASH("Cash"),
    BANK("Bank"),
    CARD("Card"),
    CHEQUE("Cheque");

    private final String label;

    PaymentMethod(String label) {
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