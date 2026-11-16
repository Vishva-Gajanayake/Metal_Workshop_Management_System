package lk.ijse.metalworkshopmanagementsystem.model.tbl;

public class SuppliersTbl {
    
    private String supplierId;
    private String supplierName;
    private String contactPerson;
    private String phone;
    private String address;

    public SuppliersTbl() {
    }

    public SuppliersTbl(String supplierId, String supplierName, String contactPerson, String phone, String address) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
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

    @Override
    public String toString() {
        return "SuppliersTbl{" + "supplierId=" + supplierId + ", supplierName=" + supplierName + ", contactPerson=" + contactPerson + ", phone=" + phone + ", address=" + address + '}';
    }
}
