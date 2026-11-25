package lk.ijse.metalworkshopmanagementsystem.model;

public enum QuotationStatus {
    
    DRAFT("Draft"),
    SENT("Sent"),
    ACCEPTED("Accepted"),
    EXPIRED("Expired"),
    REJECTED("Rejected");

    private final String label;

    QuotationStatus(String label) {
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
