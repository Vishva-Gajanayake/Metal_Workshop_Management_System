package lk.ijse.metalworkshopmanagementsystem.model.tbl;

import lk.ijse.metalworkshopmanagementsystem.model.MaterialStatus;
import lk.ijse.metalworkshopmanagementsystem.model.MaterialType;
import lk.ijse.metalworkshopmanagementsystem.model.UnitOfMaterial;

public class MaterialStockTbl {
    
    private String materialCode;
    private String materialName;
    private MaterialType materialType;
    private UnitOfMaterial unitOfMeasure;
    private double unitCost;
    private double quantityOnHand;
    private MaterialStatus status;

    public MaterialStockTbl() {
    }

    public MaterialStockTbl(String materialCode, String materialName, MaterialType materialType, UnitOfMaterial unitOfMeasure, double unitCost, double quantityOnHand, MaterialStatus status) {
        this.materialCode = materialCode;
        this.materialName = materialName;
        this.materialType = materialType;
        this.unitOfMeasure = unitOfMeasure;
        this.unitCost = unitCost;
        this.quantityOnHand = quantityOnHand;
        this.status = status;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
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

    public MaterialStatus getStatus() {
        return status;
    }

    public void setStatus(MaterialStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MaterialStockTbl{" + "materialCode=" + materialCode + ", materialName=" + materialName + ", materialType=" + materialType + ", unitOfMeasure=" + unitOfMeasure + ", unitCost=" + unitCost + ", quantityOnHand=" + quantityOnHand + ", status=" + status + '}';
    }
}
