package lk.ijse.metalworkshopmanagementsystem.model;


public class MaterialStock {
    private int materialId;
    private String materialCode;
    private int supplierId;
    private String materialName;
    private MaterialType materialType;
    private String sizeSpec;
    private UnitOfMaterial unitOfMeasure;
    private double unitCost;
    private double quantityOnHand;
    private double reorderLevel;
    private boolean isActive;

    public MaterialStock() {
    }

    public MaterialStock(int materialId, String materialCode, int supplierId, String materialName, MaterialType materialType, String sizeSpec, UnitOfMaterial unitOfMeasure, double unitCost, double quantityOnHand, double reorderLevel, boolean isActive) {
        this.materialId = materialId;
        this.materialCode = materialCode;
        this.supplierId = supplierId;
        this.materialName = materialName;
        this.materialType = materialType;
        this.sizeSpec = sizeSpec;
        this.unitOfMeasure = unitOfMeasure;
        this.unitCost = unitCost;
        this.quantityOnHand = quantityOnHand;
        this.reorderLevel = reorderLevel;
        this.isActive = isActive;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public void setMaterialType(MaterialType materialType) {
        this.materialType = materialType;
    }

    public String getSizeSpec() {
        return sizeSpec;
    }

    public void setSizeSpec(String sizeSpec) {
        this.sizeSpec = sizeSpec;
    }

    public UnitOfMaterial getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(UnitOfMaterial unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public double getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(double quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public double getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(double reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Material{" + "materialId=" + materialId + ", materialCode=" + materialCode + ", supplierId=" + supplierId + ", materialName=" + materialName + ", materialType=" + materialType.name() + ", sizeSpec=" + sizeSpec + ", unitOfMeasure=" + unitOfMeasure.name() + ", unitCost=" + unitCost + ", quantityOnHand=" + quantityOnHand + ", reorderLevel=" + reorderLevel + ", isActive=" + isActive + '}';
    }
    
}
