package lk.ijse.metalworkshopmanagementsystem.model;

import java.time.LocalDateTime;

public class Client {
    private int clientId;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private LocalDateTime createdAt;
    private boolean isActive;

    public Client() {
    }

    public Client(int clientId, String fullName, String phone, String email, String address, LocalDateTime createdAt, boolean isActive) {
        this.clientId = clientId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Client{" + "clientId=" + clientId + ", fullName=" + fullName + ", phone=" + phone + ", email=" + email + ", address=" + address + ", createdAt=" + createdAt + ", isActive=" + isActive + '}';
    }
    
}
