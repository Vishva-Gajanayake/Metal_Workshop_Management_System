package lk.ijse.metalworkshopmanagementsystem.model.tbl;

import java.time.LocalDate;

public class InquiryTbl {
    
    private String inquiryId;
    private String clientName;
    private String categoryName;
    private LocalDate siteVisitDate;
    private String inquiryStatus;

    public InquiryTbl() {
    }

    public InquiryTbl(String inquiryId, String clientName, String categoryName, LocalDate siteVisitDate, String inquiryStatus) {
        this.inquiryId = inquiryId;
        this.clientName = clientName;
        this.categoryName = categoryName;
        this.siteVisitDate = siteVisitDate;
        this.inquiryStatus = inquiryStatus;
    }

    public String getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(String inquiryId) {
        this.inquiryId = inquiryId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public LocalDate getSiteVisitDate() {
        return siteVisitDate;
    }

    public void setSiteVisitDate(LocalDate siteVisitDate) {
        this.siteVisitDate = siteVisitDate;
    }

    public String getInquiryStatus() {
        return inquiryStatus;
    }

    public void setInquiryStatus(String inquiryStatus) {
        this.inquiryStatus = inquiryStatus;
    }

    @Override
    public String toString() {
        return "InquiryTbl{" + "inquiryId=" + inquiryId + ", clientId=" + clientName + ", categoryId=" + categoryName + ", siteVisitDate=" + siteVisitDate + ", inquiryStatus=" + inquiryStatus + '}';
    }
}
