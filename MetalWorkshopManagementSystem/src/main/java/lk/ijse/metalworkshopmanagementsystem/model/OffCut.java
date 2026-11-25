package lk.ijse.metalworkshopmanagementsystem.model;

import java.time.LocalDate;

public class OffCut {
    
    private int offCutId;
    private int materialId;
    private int sourceJobId;
    private double length;
    private String rackLocation;
    private OffCutStatus status;
    private LocalDate recordedDate;

    public OffCut() {
    }

    public OffCut(int offCutId, int materialId, int sourceJobId, double length, String rackLocation, OffCutStatus status, LocalDate recordedDate) {
        this.offCutId = offCutId;
        this.materialId = materialId;
        this.sourceJobId = sourceJobId;
        this.length = length;
        this.rackLocation = rackLocation;
        this.status = status;
        this.recordedDate = recordedDate;
    }

    public int getOffCutId() {
        return offCutId;
    }

    public void setOffCutId(int offCutId) {
        this.offCutId = offCutId;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public int getSourceJobId() {
        return sourceJobId;
    }

    public void setSourceJobId(int sourceJobId) {
        this.sourceJobId = sourceJobId;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public String getRackLocation() {
        return rackLocation;
    }

    public void setRackLocation(String rackLocation) {
        this.rackLocation = rackLocation;
    }

    public OffCutStatus getStatus() {
        return status;
    }

    public void setStatus(OffCutStatus status) {
        this.status = status;
    }

    public LocalDate getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(LocalDate recordedDate) {
        this.recordedDate = recordedDate;
    }

    @Override
    public String toString() {
        return "OffCut{" + "offCutId=" + offCutId + ", materialId=" + materialId + ", sourceJobId=" + sourceJobId + ", length=" + length + ", rackLocation=" + rackLocation + ", status=" + status + ", recordedDate=" + recordedDate + '}';
    }
}
