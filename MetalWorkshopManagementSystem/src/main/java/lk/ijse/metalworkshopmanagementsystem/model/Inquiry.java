package lk.ijse.metalworkshopmanagementsystem.model;

import java.time.LocalDateTime;
import java.time.LocalDate;

public class Inquiry {
    private String inquiryId;
    private String clientId;
    private String categoryId;
    private LocalDateTime inquiryDate;
    private String siteLocation;
    private String description;
    private LocalDate siteVisitDate;
    private InquiryStatus inquiryStatus;

    public Inquiry() {
    }

    public Inquiry(String inquiryId, String clientId, String categoryId, LocalDateTime inquiryDate, String siteLocation, String description, LocalDate siteVisitDate, InquiryStatus inquiryStatus) {
        this.inquiryId = inquiryId;
        this.clientId = clientId;
        this.categoryId = categoryId;
        this.inquiryDate = inquiryDate;
        this.siteLocation = siteLocation;
        this.description = description;
        this.siteVisitDate = siteVisitDate;
        this.inquiryStatus = inquiryStatus;
    }
    
    public Inquiry(String clientId, String categoryId, LocalDateTime inquiryDate, String siteLocation, String description, LocalDate siteVisitDate, InquiryStatus inquiryStatus) {
        
        this.clientId = clientId;
        this.categoryId = categoryId;
        this.inquiryDate = inquiryDate;
        this.siteLocation = siteLocation;
        this.description = description;
        this.siteVisitDate = siteVisitDate;
        this.inquiryStatus = inquiryStatus;
    }

    public String getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(String inquiryId) {
        this.inquiryId = inquiryId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public LocalDateTime getInquiryDate() {
        return inquiryDate;
    }

    public void setInquiryDate(LocalDateTime inquiryDate) {
        this.inquiryDate = inquiryDate;
    }

    public String getSiteLocation() {
        return siteLocation;
    }

    public void setSiteLocation(String siteLocation) {
        this.siteLocation = siteLocation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getSiteVisitDate() {
        return siteVisitDate;
    }

    public void setSiteVisitDate(LocalDate siteVisitDate) {
        this.siteVisitDate = siteVisitDate;
    }

    public InquiryStatus getInquiryStatus() {
        return inquiryStatus;
    }

    public void setInquiryStatus(InquiryStatus inquiryStatus) {
        this.inquiryStatus = inquiryStatus;
    }

    @Override
    public String toString() {
        return "Inquiry{" + "inquiryId=" + inquiryId + ", clientId=" + clientId + ", categoryId=" + categoryId + ", inquiryDate=" + inquiryDate + ", siteLocation=" + siteLocation + ", description=" + description + ", siteVisitDate=" + siteVisitDate + ", inquiryStatus=" + inquiryStatus + '}';
    }
    
}
