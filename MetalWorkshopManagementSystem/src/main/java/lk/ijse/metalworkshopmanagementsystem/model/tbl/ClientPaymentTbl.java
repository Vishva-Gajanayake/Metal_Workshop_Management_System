package lk.ijse.metalworkshopmanagementsystem.model.tbl;

public class ClientPaymentTbl {

    private String paymentDate;
    private String paymentType;
    private String amountFormatted;
    private String paymentMethod;
    private String receivedBy;

    public ClientPaymentTbl() {
    }

    public ClientPaymentTbl(String paymentDate, String paymentType, String amountFormatted,
                            String paymentMethod, String receivedBy) {
        this.paymentDate = paymentDate;
        this.paymentType = paymentType;
        this.amountFormatted = amountFormatted;
        this.paymentMethod = paymentMethod;
        this.receivedBy = receivedBy;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getAmountFormatted() {
        return amountFormatted;
    }

    public void setAmountFormatted(String amountFormatted) {
        this.amountFormatted = amountFormatted;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }
}