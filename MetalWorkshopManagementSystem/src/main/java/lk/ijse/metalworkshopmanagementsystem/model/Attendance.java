package lk.ijse.metalworkshopmanagementsystem.model;

import java.time.LocalDate;

public class Attendance {
    private int attendanceId;
    private int workerId;
    private LocalDate attendanceDate;
    private AttendanceStatus attendanceStatus;
    private double overtime;

    public Attendance() {
    }

    public Attendance(int attendanceId, int workerId, LocalDate attendanceDate, AttendanceStatus attendanceStatus, double overtime) {
        this.attendanceId = attendanceId;
        this.workerId = workerId;
        this.attendanceDate = attendanceDate;
        this.attendanceStatus = attendanceStatus;
        this.overtime = overtime;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
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
        return "Attendance{" + "attendanceId=" + attendanceId + ", workerId=" + workerId + ", attendanceDate=" + attendanceDate + ", attendanceStatus=" + attendanceStatus + ", overtime=" + overtime + '}';
    }
}
