package lk.ijse.metalworkshopmanagementsystem.model.tbl;

public class EstimateItemTbl {
    
    private String material_id;
    private double required_qty;
    private double unit_cost;
    private double line_cost;
    private String offcut_id;

    public EstimateItemTbl() {
    }

    public EstimateItemTbl(String material_id, double required_qty, double unit_cost, double line_cost, String offcut_id) {
        this.material_id = material_id;
        this.required_qty = required_qty;
        this.unit_cost = unit_cost;
        this.line_cost = line_cost;
        this.offcut_id = offcut_id;
    }

    public String getMaterial_id() {
        return material_id;
    }

    public void setMaterial_id(String material_id) {
        this.material_id = material_id;
    }

    public double getRequired_qty() {
        return required_qty;
    }

    public void setRequired_qty(double required_qty) {
        this.required_qty = required_qty;
    }

    public double getUnit_cost() {
        return unit_cost;
    }

    public void setUnit_cost(double unit_cost) {
        this.unit_cost = unit_cost;
    }

    public double getLine_cost() {
        return line_cost;
    }

    public void setLine_cost(double line_cost) {
        this.line_cost = line_cost;
    }

    public String getOffcut_id() {
        return offcut_id;
    }

    public void setOffcut_id(String offcut_id) {
        this.offcut_id = offcut_id;
    }

    @Override
    public String toString() {
        return "EstimateItemTbl{" + "material_id=" + material_id + ", required_qty=" + required_qty + ", unit_cost=" + unit_cost + ", line_cost=" + line_cost + ", offcut_id=" + offcut_id + '}';
    }
}
