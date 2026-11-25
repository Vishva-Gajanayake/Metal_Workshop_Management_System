package lk.ijse.metalworkshopmanagementsystem.model.tbl;

import lk.ijse.metalworkshopmanagementsystem.model.OffCutStatus;

public class OffCutTbl {
    
    private String offCutId;
    private String materialId;
    private String sourceJobId;
    private double length;
    private String rackLocation;
    private OffCutStatus status;

    public OffCutTbl() {
    }

    public OffCutTbl(String offCutId, String materialId, String sourceJobId, double length, String rackLocation, OffCutStatus status) {
        this.offCutId = offCutId;
        this.materialId = materialId;
        this.sourceJobId = sourceJobId;
        this.length = length;
        this.rackLocation = rackLocation;
        this.status = status;
    }

    public String getOffCutId() {
        return offCutId;
    }

    public void setOffCutId(String offCutId) {
        this.offCutId = offCutId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getSourceJobId() {
        return sourceJobId;
    }

    public void setSourceJobId(String sourceJobId) {
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

    @Override
    public String toString() {
        return "OffCutTbl{" + "offCutId=" + offCutId + ", materialId=" + materialId + ", sourceJobId=" + sourceJobId + ", length=" + length + ", rackLocation=" + rackLocation + ", status=" + status + '}';
    }
}
