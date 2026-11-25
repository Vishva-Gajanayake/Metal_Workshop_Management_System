package lk.ijse.metalworkshopmanagementsystem.model.tbl;

import java.time.LocalDate;
import lk.ijse.metalworkshopmanagementsystem.model.AttendanceStatus;

public class AttendanceTbl {
    private String worker;
    private LocalDate attendanceDate;
    private AttendanceStatus attendanceStatus;
    private double overtime;

    public AttendanceTbl() {
    }

    public AttendanceTbl(LocalDate attendanceDate, String worker, AttendanceStatus attendanceStatus, double overtime) {
        this.worker = worker;
        this.attendanceDate = attendanceDate;
        this.attendanceStatus = attendanceStatus;
        this.overtime = overtime;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public AttendanceStatus getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(AttendanceStatus attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public double getOvertime() {
        return overtime;
    }

    public void setOvertime(double overtime) {
        this.overtime = overtime;
    }

    @Override
    public String toString() {
        return "AttendanceTbl{" + "worker=" + worker + ", attendanceDate=" + attendanceDate + ", attendanceStatus=" + attendanceStatus + ", overtime=" + overtime + '}';
    }
}
