package lk.ijse.metalworkshopmanagementsystem.model;

import java.time.LocalDate;

public class WageAdvances {
    
    private String advancesId;
    private String workerId;
    private String wagePaymentId;
    private LocalDate advanceDate;
    private Double amount;
    private String reason;

    public WageAdvances() {
    }

    public WageAdvances(String advancesId, String workerId, String wagePaymentId, LocalDate advanceDate, Double amount, String reason) {
        this.advancesId = advancesId;
        this.workerId = workerId;
        this.wagePaymentId = wagePaymentId;
        this.advanceDate = advanceDate;
        this.amount = amount;
        this.reason = reason;
    }

    public String getAdvancesId() {
        return advancesId;
    }

    public void setAdvancesId(String advancesId) {
        this.advancesId = advancesId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getWagePaymentId() {
        return wagePaymentId;
    }

    public void setWagePaymentId(String wagePaymentId) {
        this.wagePaymentId = wagePaymentId;
    }

    public LocalDate getAdvanceDate() {
        return advanceDate;
    }

    public void setAdvanceDate(LocalDate advanceDate) {
        this.advanceDate = advanceDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "WageAdvances{" + "advancesId=" + advancesId + ", workerId=" + workerId + ", wagePaymentId=" + wagePaymentId + ", advanceDate=" + advanceDate + ", amount=" + amount + ", reason=" + reason + '}';
    }
}
