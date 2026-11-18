package lk.ijse.metalworkshopmanagementsystem.model.tbl;

import lk.ijse.metalworkshopmanagementsystem.model.Role;

public class WorkerTbl {
    
    private String workerId;
    private String fullName;
    private String role;
    private String phone;
    private double dailyWage;

    public WorkerTbl() {
    }

    public WorkerTbl(String workerId, String fullName, String role, String phone, double dailyWage) {
        this.workerId = workerId;
        this.fullName = fullName;
        this.role = role;
        this.phone = phone;
        this.dailyWage = dailyWage;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
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

    @Override
    public String toString() {
        return "WorkerTbl{" + "workerId=" + workerId + ", fullName=" + fullName + ", role=" + role + ", phone=" + phone + ", dailyWage=" + dailyWage + '}';
    }
}
