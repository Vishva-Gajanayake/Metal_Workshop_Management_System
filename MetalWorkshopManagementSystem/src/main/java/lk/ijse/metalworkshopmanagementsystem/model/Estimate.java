package lk.ijse.metalworkshopmanagementsystem.model;

import java.time.LocalDateTime;

public class Estimate {
    private int estimateId;
    private int inquiryId;
    private int createdBy;
    private LocalDateTime estimateDate;
    private double width;
    private double height;
    private double length;
    private int quantity;
    private double wastagePct;
    private double materialCost;
    private double labourCost;
    private double oveheadCost;
    private double totalCost;

    public Estimate() {
    }

    public Estimate(int estimateId, int inquiryId, int createdBy, LocalDateTime estimateDate, double width, double height, double length, int quantity, double wastagePct, double materialCost, double labourCost, double oveheadCost, double totalCost) {
        this.estimateId = estimateId;
        this.inquiryId = inquiryId;
        this.createdBy = createdBy;
        this.estimateDate = estimateDate;
        this.width = width;
        this.height = height;
        this.length = length;
        this.quantity = quantity;
        this.wastagePct = wastagePct;
        this.materialCost = materialCost;
        this.labourCost = labourCost;
        this.oveheadCost = oveheadCost;
        this.totalCost = totalCost;
    }

    public int getEstimateId() {
        return estimateId;
    }

    public void setEstimateId(int estimateId) {
        this.estimateId = estimateId;
    }

    public int getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(int inquiryId) {
        this.inquiryId = inquiryId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getEstimateDate() {
        return estimateDate;
    }

    public void setEstimateDate(LocalDateTime estimateDate) {
        this.estimateDate = estimateDate;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getWastagePct() {
        return wastagePct;
    }

    public void setWastagePct(double wastagePct) {
        this.wastagePct = wastagePct;
    }

    public double getMaterialCost() {
        return materialCost;
    }

    public void setMaterialCost(double materialCost) {
        this.materialCost = materialCost;
    }

    public double getLabourCost() {
        return labourCost;
    }

    public void setLabourCost(double labourCost) {
        this.labourCost = labourCost;
    }

    public double getOveheadCost() {
        return oveheadCost;
    }

    public void setOveheadCost(double oveheadCost) {
        this.oveheadCost = oveheadCost;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    @Override
    public String toString() {
        return "Estimate{" + "estimateId=" + estimateId + ", inquiryId=" + inquiryId + ", createdBy=" + createdBy + ", estimateDate=" + estimateDate + ", width=" + width + ", height=" + height + ", length=" + length + ", quantity=" + quantity + ", wastagePct=" + wastagePct + ", materialCost=" + materialCost + ", labourCost=" + labourCost + ", oveheadCost=" + oveheadCost + ", totalCost=" + totalCost + '}';
    }
    
}
