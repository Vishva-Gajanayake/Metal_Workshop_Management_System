package lk.ijse.metalworkshopmanagementsystem.model.tbl;

import java.time.LocalDate;

public class JobOrderTbl {
    
    private String jobNo;
    private String clientName;
    private LocalDate dueDate;
    private String status;

    public JobOrderTbl() {
    }

    public JobOrderTbl(String jobNo, String clientName, LocalDate dueDate, String status) {
        this.jobNo = jobNo;
        this.clientName = clientName;
        this.dueDate = dueDate;
        this.status = status;
    }

    public String getJobNo() {
        return jobNo;
    }

    public String getClientName() {
        return clientName;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setJobNo(String jobNo) {
        this.jobNo = jobNo;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "JobOrderTbl{" + "jobNo=" + jobNo + ", clientName=" + clientName + ", dueDate=" + dueDate + ", status=" + status + '}';
    }
}
