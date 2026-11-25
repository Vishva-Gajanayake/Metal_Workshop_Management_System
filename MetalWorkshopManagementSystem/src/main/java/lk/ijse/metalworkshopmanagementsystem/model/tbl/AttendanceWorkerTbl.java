package lk.ijse.metalworkshopmanagementsystem.model.tbl;

import lk.ijse.metalworkshopmanagementsystem.model.AttendanceStatus;

public class AttendanceWorkerTbl {
    
    private String worker;
    private AttendanceStatus status;
    private double overtime;

    public AttendanceWorkerTbl() {
    }

    public AttendanceWorkerTbl(String workerId, AttendanceStatus status, double overtime) {
        this.worker = workerId;
        this.status = status;
        this.overtime = overtime;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public double getOvertime() {
        return overtime;
    }

    public void setOvertime(double overtime) {
        this.overtime = overtime;
    }

    @Override
    public String toString() {
        return "AttendanceWorkerTbl{" + "workerId=" + worker + ", status=" + status + ", overtime=" + overtime + '}';
    }
}
