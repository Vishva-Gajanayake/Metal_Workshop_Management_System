package lk.ijse.metalworkshopmanagementsystem.model;

import java.time.LocalDate;

public class Worker {
    private int workerId;
    private int userId;
    private String fullName;
    private String roleTitle;
    private String phone;
    private double dailyWage;
    private LocalDate joinDate;
    private boolean isActive;

    public Worker() {
    }

    public Worker(int workerId, int userId, String fullName, String roleTitle, String phone, double dailyWage, LocalDate joinDate, boolean isActive) {
        this.workerId = workerId;
        this.userId = userId;
        this.fullName = fullName;
        this.roleTitle = roleTitle;
        this.phone = phone;
        this.dailyWage = dailyWage;
        this.joinDate = joinDate;
        this.isActive = isActive;
    }

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
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
        return "Worker{" + "workerId=" + workerId + ", userId=" + userId + ", fullName=" + fullName + ", roleTitle=" + roleTitle + ", phone=" + phone + ", dailyWage=" + dailyWage + ", joinDate=" + joinDate + ", isActive=" + isActive + '}';
    }
    
}
