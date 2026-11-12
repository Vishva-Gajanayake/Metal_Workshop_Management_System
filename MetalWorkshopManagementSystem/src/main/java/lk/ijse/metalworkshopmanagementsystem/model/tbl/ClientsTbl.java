package lk.ijse.metalworkshopmanagementsystem.model.tbl;

public class ClientsTbl {
    
    private String clientId;
    private String fullName;
    private String phone;
    private String email;
    private String address;

    public ClientsTbl() {
    }

    public ClientsTbl(String clientId, String fullName, String phone, String email, String address) {
        this.clientId = clientId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "ClientsTbl{" + "clientId=" + clientId + ", fullName=" + fullName + ", phone=" + phone + ", email=" + email + ", address=" + address + '}';
    }
    
    
}
