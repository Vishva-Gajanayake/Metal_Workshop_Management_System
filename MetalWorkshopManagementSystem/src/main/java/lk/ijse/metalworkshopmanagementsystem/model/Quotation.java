package lk.ijse.metalworkshopmanagementsystem.model;

import java.time.LocalDate;

public class Quotation {
    private int quotationId;
    private int estimateId;
    private String quotationNo;
    private LocalDate quotationDate;
    private LocalDate validUntil;
    private double marginPct;
    private double discountAmount;
    private double totalAmount;
    private double advanceRequired;
    private QuotationStatus status;

    public Quotation() {
    }

    public Quotation(int quotationId, int estimateId, String quotationNo, LocalDate quotationDate, LocalDate validUntil, double marginPct, double discountAmount, double totalAmount, double advanceRequired, QuotationStatus status) {
        this.quotationId = quotationId;
        this.estimateId = estimateId;
        this.quotationNo = quotationNo;
        this.quotationDate = quotationDate;
        this.validUntil = validUntil;
        this.marginPct = marginPct;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.advanceRequired = advanceRequired;
        this.status = status;
    }

    public int getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(int quotationId) {
        this.quotationId = quotationId;
    }

    public int getEstimateId() {
        return estimateId;
    }

    public void setEstimateId(int estimateId) {
        this.estimateId = estimateId;
    }

    public String getQuotationNo() {
        return quotationNo;
    }

    public void setQuotationNo(String quotationNo) {
        this.quotationNo = quotationNo;
    }

    public LocalDate getQuotationDate() {
        return quotationDate;
    }

    public void setQuotationDate(LocalDate quotationDate) {
        this.quotationDate = quotationDate;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public double getMarginPct() {
        return marginPct;
    }

    public void setMarginPct(double marginPct) {
        this.marginPct = marginPct;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getAdvanceRequired() {
        return advanceRequired;
    }

    public void setAdvanceRequired(double advanceRequired) {
        this.advanceRequired = advanceRequired;
    }

    public QuotationStatus getStatus() {
        return status;
    }

    public void setStatus(QuotationStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Quotation{" + "quotationId=" + quotationId + ", estimateId=" + estimateId + ", quotationNo=" + quotationNo + ", quotationDate=" + quotationDate + ", validUntil=" + validUntil + ", marginPct=" + marginPct + ", discountAmount=" + discountAmount + ", totalAmount=" + totalAmount + ", advanceRequired=" + advanceRequired + ", status=" + status + '}';
    }
    
}
