package lk.ijse.metalworkshopmanagementsystem.model;

public enum InquiryStatus {
    
    NEW("New"),
    SITE_VISITED("Site Visited"),
    QUOTED("Quoted"),
    CONVERTED("Converted"),
    CLOSED("Closed");

    private final String label;

    InquiryStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
