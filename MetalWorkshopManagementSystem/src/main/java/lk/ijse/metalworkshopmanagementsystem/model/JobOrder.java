package lk.ijse.metalworkshopmanagementsystem.model;

import java.time.LocalDate;

public class JobOrder {
    
    private int jobId;
    private int quotationId;
    private String jobNo;
    private String siteAddress;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate compleDate;
    private JobStatus status;

    public JobOrder() {
    }

    public JobOrder(int jobId, int quotationId, String jobNo, String siteAddress, LocalDate startDate, LocalDate dueDate, LocalDate compleDate, JobStatus status) {
        this.jobId = jobId;
        this.quotationId = quotationId;
        this.jobNo = jobNo;
        this.siteAddress = siteAddress;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.compleDate = compleDate;
        this.status = status;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(int quotationId) {
        this.quotationId = quotationId;
    }

    public String getJobNo() {
        return jobNo;
    }

    public void setJobNo(String jobNo) {
        this.jobNo = jobNo;
    }

    public String getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getCompleDate() {
        return compleDate;
    }

    public void setCompleDate(LocalDate compleDate) {
        this.compleDate = compleDate;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "JobOrder{" + "jobId=" + jobId + ", quotationId=" + quotationId + ", jobNo=" + jobNo + ", siteAddress=" + siteAddress + ", startDate=" + startDate + ", dueDate=" + dueDate + ", compleDate=" + compleDate + ", status=" + status + '}';
    }
}
