package lk.ijse.metalworkshopmanagementsystem.model;

public class Supplier {
    private int supplierId;
    private String suppliierName;
    private String contactPerson;
    private String phone;
    private String address;
    private boolean isActive;

    public Supplier() {
    }

    public Supplier(int supplierId, String suppliierName, String contactPerson, String phone, String address, boolean isActive) {
        this.supplierId = supplierId;
        this.suppliierName = suppliierName;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.address = address;
        this.isActive = isActive;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSuppliierName() {
        return suppliierName;
    }

    public void setSuppliierName(String suppliierName) {
        this.suppliierName = suppliierName;
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
        return "Supplier{" + "supplierId=" + supplierId + ", suppliierName=" + suppliierName + ", contactPerson=" + contactPerson + ", phone=" + phone + ", address=" + address + ", isActive=" + isActive + '}';
    }
    
}
