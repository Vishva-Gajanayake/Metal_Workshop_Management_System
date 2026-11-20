package lk.ijse.metalworkshopmanagementsystem.model.tbl;

public class GoodsReceivedItemTbl {
    
    private String material;
    private double qty;
    private double unitCost;
    private double lineCost;

    public GoodsReceivedItemTbl() {
    }

    public GoodsReceivedItemTbl(String material, double qty, double unitCost, double lineCost) {
        this.material = material;
        this.qty = qty;
        this.unitCost = unitCost;
        this.lineCost = lineCost;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
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
        return "GoodsReceivedItemTbl{" + "material=" + material + ", qty=" + qty + ", unitCost=" + unitCost + ", lineCost=" + lineCost + '}';
    }
}
