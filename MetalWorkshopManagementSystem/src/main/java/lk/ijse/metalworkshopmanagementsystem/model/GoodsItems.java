package lk.ijse.metalworkshopmanagementsystem.model;

public class GoodsItems {
    
    private int grnItemId;
    private int grnId;
    private int materialId;
    private double qtyReceived;
    private double unitCost;
    private double lineCost;

    public GoodsItems() {
    }

    public GoodsItems(int grnItemId, int grnId, int materialId, double qtyReceived, double unitCost, double lineCost) {
        this.grnItemId = grnItemId;
        this.grnId = grnId;
        this.materialId = materialId;
        this.qtyReceived = qtyReceived;
        this.unitCost = unitCost;
        this.lineCost = lineCost;
    }

    public int getGrnItemId() {
        return grnItemId;
    }

    public void setGrnItemId(int grnItemId) {
        this.grnItemId = grnItemId;
    }

    public int getGrnId() {
        return grnId;
    }

    public void setGrnId(int grnId) {
        this.grnId = grnId;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public double getQtyReceived() {
        return qtyReceived;
    }

    public void setQtyReceived(double qtyReceived) {
        this.qtyReceived = qtyReceived;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public double getLineCost() {
        return lineCost;
    }

    public void setLineCost(double lineCost) {
        this.lineCost = lineCost;
    }

    @Override
    public String toString() {
        return "GoodsItems{" + "grnItemId=" + grnItemId + ", grnId=" + grnId + ", materialId=" + materialId + ", qtyReceived=" + qtyReceived + ", unitCost=" + unitCost + ", lineCost=" + lineCost + '}';
    }
    
    
}
