package lk.ijse.metalworkshopmanagementsystem.model;

import java.time.LocalDate;

public class Worker {
    private String workerId;
    private String userId;
    private String fullName;
    private Role role;
    private String phone;
    private double dailyWage;
    private LocalDate joinDate;
    private boolean isActive;

    public Worker() {
    }

    public Worker(String workerId, String userId, String fullName, Role role, String phone, double dailyWage, LocalDate joinDate, boolean isActive) {
        this.workerId = workerId;
        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
        this.phone = phone;
        this.dailyWage = dailyWage;
        this.joinDate = joinDate;
        this.isActive = isActive;
    }
    
    public Worker(String userId, String fullName, Role role, String phone, double dailyWage, LocalDate joinDate, boolean isActive) {
        
        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
        this.phone = phone;
        this.dailyWage = dailyWage;
        this.joinDate = joinDate;
        this.isActive = isActive;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRoleTitle() {
        return role;
    }

    public void setRoleTitle(Role role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getDailyWage() {
        return dailyWage;
    }

    public void setDailyWage(double dailyWage) {
        this.dailyWage = dailyWage;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Worker{" + "workerId=" + workerId + ", userId=" + userId + ", fullName=" + fullName + ", roleTitle=" + role + ", phone=" + phone + ", dailyWage=" + dailyWage + ", joinDate=" + joinDate + ", isActive=" + isActive + '}';
    }
    
}
