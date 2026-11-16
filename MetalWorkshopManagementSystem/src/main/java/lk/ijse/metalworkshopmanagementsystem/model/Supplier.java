package lk.ijse.metalworkshopmanagementsystem.model;

public class Supplier {
    private String supplierId;
    private String supplierName;
    private String contactPerson;
    private String phone;
    private String address;
    private boolean isActive;

    public Supplier() {
    }

    public Supplier(String suppliierName, String contactPerson, String phone, String address, boolean isActive) {
        
        this.supplierName = suppliierName;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.address = address;
        this.isActive = isActive;
    }
    
    public Supplier(String supplierId, String suppliierName, String contactPerson, String phone, String address) {
        this.supplierId = supplierId;
        this.supplierName = suppliierName;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.address = address;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Supplier{" + "supplierId=" + supplierId + ", suppliierName=" + supplierName + ", contactPerson=" + contactPerson + ", phone=" + phone + ", address=" + address + ", isActive=" + isActive + '}';
    }
    
}
