package lk.ijse.metalworkshopmanagementsystem.model;

import java.time.LocalDate;
import java.util.List;

public class GoodsReceived {
    private int grnId;
    private int supplierId;
    private int receivedBy;
    private LocalDate receivedDateTime;
    private String invoiceRef;
    private double totalAmount;
    private List<GoodsItems> goodsItemsList;

    public GoodsReceived() {
    }

    public GoodsReceived(int grnId, int supplierId, int receivedBy, LocalDate receivedDateTime, String invoiceRef, double totalAmount, List<GoodsItems> goodsItemsList) {
        this.grnId = grnId;
        this.supplierId = supplierId;
        this.receivedBy = receivedBy;
        this.receivedDateTime = receivedDateTime;
        this.invoiceRef = invoiceRef;
        this.totalAmount = totalAmount;
        this.goodsItemsList = goodsItemsList;
    }

    public int getGrnId() {
        return grnId;
    }

    public void setGrnId(int grnId) {
        this.grnId = grnId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(int receivedBy) {
        this.receivedBy = receivedBy;
    }

    public LocalDate getReceivedDateTime() {
        return receivedDateTime;
    }

    public void setReceivedDateTime(LocalDate receivedDateTime) {
        this.receivedDateTime = receivedDateTime;
    }

    public String getInvoiceRef() {
        return invoiceRef;
    }

    public void setInvoiceRef(String invoiceRef) {
        this.invoiceRef = invoiceRef;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<GoodsItems> getGoodsItemsList() {
        return goodsItemsList;
    }

    public void setGoodsItemsList(List<GoodsItems> goodsItemsList) {
        this.goodsItemsList = goodsItemsList;
    }

    @Override
    public String toString() {
        return "GoodsReceived{" + "grnId=" + grnId + ", supplierId=" + supplierId + ", receivedBy=" + receivedBy + ", receivedDateTime=" + receivedDateTime + ", invoiceRef=" + invoiceRef + ", totalAmount=" + totalAmount + ", goodsItemsList=" + goodsItemsList + '}';
    }
}
