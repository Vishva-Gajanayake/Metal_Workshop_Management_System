package lk.ijse.metalworkshopmanagementsystem.model;

import java.time.LocalDate;

public class ClientPayment {

    private int paymentId;
    private int jobId;
    private PaymentType paymentType;
    private double amount;
    private LocalDate paymentDate;
    private PaymentMethod paymentMethod;
    private String receivedBy;

    public ClientPayment() {
    }

    public ClientPayment(int paymentId, int jobId, PaymentType paymentType, double amount,
                         LocalDate paymentDate, PaymentMethod paymentMethod, String receivedBy) {
        this.paymentId = paymentId;
        this.jobId = jobId;
        this.paymentType = paymentType;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.receivedBy = receivedBy;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    @Override
    public String toString() {
        return "ClientPayment{" + "paymentId=" + paymentId + ", jobId=" + jobId
                + ", paymentType=" + paymentType + ", amount=" + amount
                + ", paymentDate=" + paymentDate + ", paymentMethod=" + paymentMethod
                + ", receivedBy=" + receivedBy + '}';
    }
}