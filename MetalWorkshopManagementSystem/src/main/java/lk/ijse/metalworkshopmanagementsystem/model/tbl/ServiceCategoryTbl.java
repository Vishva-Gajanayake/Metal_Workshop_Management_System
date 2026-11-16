package lk.ijse.metalworkshopmanagementsystem.model.tbl;

public class ServiceCategoryTbl {
    
    private String categoryId;
    private String categoryName;
    private String unitOfMeasure;
    private double labourRate;
    private double defaultWastagePct;

    public ServiceCategoryTbl() {
    }

    public ServiceCategoryTbl(String categoryId, String categoryName, String unitOfMeasure, double labourRate, double defaultWastagePct) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.unitOfMeasure = unitOfMeasure;
        this.labourRate = labourRate;
        this.defaultWastagePct = defaultWastagePct;
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

    @Override
    public String toString() {
        return "ServiceCategoryTbl{" + "categoryId=" + categoryId + ", categoryName=" + categoryName + ", unitOfMeasure=" + unitOfMeasure + ", labourRate=" + labourRate + ", defaultWastagePct=" + defaultWastagePct + '}';
    }
    
}
