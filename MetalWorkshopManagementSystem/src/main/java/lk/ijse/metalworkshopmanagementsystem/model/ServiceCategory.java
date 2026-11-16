package lk.ijse.metalworkshopmanagementsystem.model;

public class ServiceCategory {
    private String categoryId;
    private String categoryName;
    private UnitOfMeasure unitOfMeasure;
    private double labourRate;
    private double defaultWastagePct;
    private boolean isActive;

    public ServiceCategory() {
    }

    public ServiceCategory(String categoryId, String categoryName, UnitOfMeasure unitOfMeasure, double labourRate, double defaultWastagePct) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.unitOfMeasure = unitOfMeasure;
        this.labourRate = labourRate;
        this.defaultWastagePct = defaultWastagePct;
    }
    
    public ServiceCategory(String categoryName, UnitOfMeasure unitOfMeasure, double labourRate, double defaultWastagePct, boolean isActive) {
        this.categoryName = categoryName;
        this.unitOfMeasure = unitOfMeasure;
        this.labourRate = labourRate;
        this.defaultWastagePct = defaultWastagePct;
        this.isActive = isActive;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
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
