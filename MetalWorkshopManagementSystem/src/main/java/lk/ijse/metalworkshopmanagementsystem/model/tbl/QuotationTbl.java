package lk.ijse.metalworkshopmanagementsystem.model.tbl;

import java.time.LocalDate;
import lk.ijse.metalworkshopmanagementsystem.model.QuotationStatus;

public class QuotationTbl {
    
    private String client;
    private String quotationNo;
    private double totalAmount;
    private LocalDate validUntil;
    private QuotationStatus status;

    public QuotationTbl() {
    }

    public QuotationTbl(String clientId, String quotationNo, LocalDate validUntil, double totalAmount, QuotationStatus status) {
        this.client = clientId;
        this.quotationNo = quotationNo;
        this.validUntil = validUntil;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getQuotationNo() {
        return quotationNo;
    }

    public void setQuotationNo(String quotationNo) {
        this.quotationNo = quotationNo;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public QuotationStatus getStatus() {
        return status;
    }

    public void setStatus(QuotationStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "QuotationTbl{" + "clientId=" + client + ", quotationNo=" + quotationNo + ", validUntil=" + validUntil + ", totalAmount=" + totalAmount + ", status=" + status + '}';
    }
}
