package lk.ijse.metalworkshopmanagementsystem.model;

public class ServiceCategory {
    private int categoryId;
    private String categoryName;
    private String unitOfMeasure;
    private double labourRate;
    private double defaultWastagePct;
    private boolean isActive;

    public ServiceCategory() {
    }

    public ServiceCategory(int categoryId, String categoryName, String unitOfMeasure, double labourRate, double defaultWastagePct, boolean isActive) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.unitOfMeasure = unitOfMeasure;
        this.labourRate = labourRate;
        this.defaultWastagePct = defaultWastagePct;
        this.isActive = isActive;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public double getLabourRate() {
        return labourRate;
    }

    public void setLabourRate(double labourRate) {
        this.labourRate = labourRate;
    }

    public double getDefaultWastagePct() {
        return defaultWastagePct;
    }

    public void setDefaultWastagePct(double defaultWastagePct) {
        this.defaultWastagePct = defaultWastagePct;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "ServiceCategory{" + "categoryId=" + categoryId + ", categoryName=" + categoryName + ", unitOfMeasure=" + unitOfMeasure + ", labourRate=" + labourRate + ", defaultWastagePct=" + defaultWastagePct + ", isActive=" + isActive + '}';
    }
    
}
