package lk.ijse.metalworkshopmanagementsystem.model.tbl;

import java.time.LocalDate;

public class WageAdvancesTbl {
    
    private String advancesId;
    private String worker;
    private LocalDate advanceDate;
    private Double amount;

    public WageAdvancesTbl() {
    }

    public WageAdvancesTbl(String advancesId, String worker, LocalDate advanceDate, Double amount) {
        this.advancesId = advancesId;
        this.worker = worker;
        this.advanceDate = advanceDate;
        this.amount = amount;
    }

    public String getAdvancesId() {
        return advancesId;
    }

    public void setAdvancesId(String advancesId) {
        this.advancesId = advancesId;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
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

    @Override
    public String toString() {
        return "WageAdvancesTbl{" + "advancesId=" + advancesId + ", worker=" + worker + ", advanceDate=" + advanceDate + ", amount=" + amount + '}';
    }
}
