package lk.ijse.metalworkshopmanagementsystem.model;

import java.time.LocalDateTime;

public class GoodReceived {
    private int grnId;
    private int supplierId;
    private int receivedBy;
    private LocalDateTime receivedDateTime;
    private String invoiceRef;
    private double totalAmount;

    public GoodReceived() {
    }

    public GoodReceived(int grnId, int supplierId, int receivedBy, LocalDateTime receivedDateTime, String invoiceRef, double totalAmount) {
        this.grnId = grnId;
        this.supplierId = supplierId;
        this.receivedBy = receivedBy;
        this.receivedDateTime = receivedDateTime;
        this.invoiceRef = invoiceRef;
        this.totalAmount = totalAmount;
    }

    public int getGrnId() {
        return grnId;
    }

    public void setGrnId(int grnId) {
        this.grnId = grnId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(int receivedBy) {
        this.receivedBy = receivedBy;
    }

    public LocalDateTime getReceivedDateTime() {
        return receivedDateTime;
    }

    public void setReceivedDateTime(LocalDateTime receivedDateTime) {
        this.receivedDateTime = receivedDateTime;
    }

    public String getInvoiceRef() {
        return invoiceRef;
    }

    public void setInvoiceRef(String invoiceRef) {
        this.invoiceRef = invoiceRef;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "GoodReceived{" + "grnId=" + grnId + ", supplierId=" + supplierId + ", receivedBy=" + receivedBy + ", receivedDateTime=" + receivedDateTime + ", invoiceRef=" + invoiceRef + ", totalAmount=" + totalAmount + '}';
    }
    
}
