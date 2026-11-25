package lk.ijse.metalworkshopmanagementsystem.model.tbl;

import java.time.LocalDate;

public class GoodsReceivedTbl {
    
    private int grnId;
    private LocalDate date;
    private String supplier;
    private String inoviceRef;
    private double total;

    public GoodsReceivedTbl() {
    }

    public GoodsReceivedTbl(int grnId, LocalDate date, String supplier, String inoviceRef, double total) {
        this.grnId = grnId;
        this.date = date;
        this.supplier = supplier;
        this.inoviceRef = inoviceRef;
        this.total = total;
    }

    public int getGrnId() {
        return grnId;
    }

    public void setGrnId(int grnId) {
        this.grnId = grnId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSuppliler(String supplier) {
        this.supplier = supplier;
    }

    public String getInoviceRef() {
        return inoviceRef;
    }

    public void setInoviceRef(String inoviceRef) {
        this.inoviceRef = inoviceRef;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "GoodsReceivedTbl{" + "grnId=" + grnId + ", date=" + date + ", supplier=" + supplier + ", inoviceRef=" + inoviceRef + ", total=" + total + '}';
    }
}
