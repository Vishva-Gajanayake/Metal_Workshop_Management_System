package lk.ijse.metalworkshopmanagementsystem.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lk.ijse.metalworkshopmanagementsystem.db.DBConnection;
import lk.ijse.metalworkshopmanagementsystem.model.ClientPayment;
import lk.ijse.metalworkshopmanagementsystem.model.PaymentMethod;
import lk.ijse.metalworkshopmanagementsystem.model.PaymentType;
import lk.ijse.metalworkshopmanagementsystem.model.tbl.ClientPaymentTbl;

public class ClientPaymentsController implements Initializable {

    private static class JobOption {
        final int jobId;
        final int quotationId;
        final String jobNo;
        final String status;
        final String label;

        JobOption(int jobId, int quotationId, String jobNo, String status, String label) {
            this.jobId = jobId;
            this.quotationId = quotationId;
            this.jobNo = jobNo;
            this.status = status;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    @FXML
    private AnchorPane clientPaymentsPane;

    @FXML
    private ComboBox<JobOption> jobCombo;

    @FXML
    private Label quotedLbl;

    @FXML
    private Label paidLbl;

    @FXML
    private Label balanceLbl;

    @FXML
    private ComboBox<PaymentType> paymentTypeCombo;

    @FXML
    private TextField amountTxtField;

    @FXML
    private DatePicker paymentDatePicker;

    @FXML
    private ComboBox<PaymentMethod> methodCombo;

    @FXML
    private Button savePaymentBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private TableView<ClientPaymentTbl> paymentHistoryTbl;

    @FXML
    private TableColumn<ClientPaymentTbl, String> colDate;

    @FXML
    private TableColumn<ClientPaymentTbl, String> colType;

    @FXML
    private TableColumn<ClientPaymentTbl, String> colAmount;

    @FXML
    private TableColumn<ClientPaymentTbl, String> colMethod;

    @FXML
    private TableColumn<ClientPaymentTbl, String> colReceivedBy;

    @FXML
    private Button generateInvoiceBtn;

    private Connection conn;
    private JobOption currentJob;
    private double quotedAmount = 0.0;
    private double paidAmount = 0.0;
    private double balanceAmount = 0.0;

    private static final String MONEY_REGEX = "^\\d{1,9}(\\.\\d{1,2})?$";
    private static final DecimalFormat CURRENCY_FMT = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter DISPLAY_DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        colType.setCellValueFactory(new PropertyValueFactory<>("paymentType"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amountFormatted"));
        colMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colReceivedBy.setCellValueFactory(new PropertyValueFactory<>("receivedBy"));

        paymentDatePicker.setValue(LocalDate.now());
        loadPaymentTypeCombo();
        loadMethodCombo();
        loadJobsCombo();
        resetSummaryAndHistory();

        jobCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                applySelectedJob(newVal);
            } else {
                resetSummaryAndHistory();
            }
        });
    }

    private void loadPaymentTypeCombo() {
        paymentTypeCombo.setItems(FXCollections.observableArrayList(PaymentType.values()));
    }

    private void loadMethodCombo() {
        methodCombo.setItems(FXCollections.observableArrayList(PaymentMethod.values()));
    }

    private void loadJobsCombo() {
        ObservableList<JobOption> jobItems = FXCollections.observableArrayList();

        try {
            conn = DBConnection.getInstance().getConnection();
            String query = "SELECT job_id, quotation_id, job_no, site_address, status "
                    + "FROM job_orders ORDER BY job_id DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int jobId = rs.getInt("job_id");
                int quotationId = rs.getInt("quotation_id");
                String jobNo = rs.getString("job_no");
                String siteAddr = rs.getString("site_address");
                String status = rs.getString("status");

                String cleanJobNo = (jobNo != null && !jobNo.trim().isEmpty())
                        ? jobNo.trim()
                        : String.format("JOB-%04d", jobId);

                String label = cleanJobNo
                        + (siteAddr != null && !siteAddr.trim().isEmpty() ? " - " + siteAddr.trim() : "");

                jobItems.add(new JobOption(jobId, quotationId, cleanJobNo, status, label));
            }

            rs.close();
            stmt.close();
            jobCombo.setItems(jobItems);

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load job orders!").show();
        }
    }

    @FXML
    void selectJobCombo(ActionEvent event) {
        JobOption selected = jobCombo.getValue();
        if (selected != null) {
            applySelectedJob(selected);
        } else {
            resetSummaryAndHistory();
        }
    }

    private void applySelectedJob(JobOption job) {
        this.currentJob = job;
        loadJobFinancialSummary(job);
        loadPaymentHistory(job.jobId);
    }

    private void loadJobFinancialSummary(JobOption job) {
        try {
            conn = DBConnection.getInstance().getConnection();
            quotedAmount = fetchQuotedAmount(job.quotationId);

            String paidQuery = "SELECT COALESCE(SUM(amount), 0) AS total_paid FROM payments WHERE job_id = ?";
            PreparedStatement paidPstmt = conn.prepareStatement(paidQuery);
            paidPstmt.setInt(1, job.jobId);
            ResultSet paidRs = paidPstmt.executeQuery();
            if (paidRs.next()) {
                paidAmount = paidRs.getDouble("total_paid");
            }
            paidRs.close();
            paidPstmt.close();

            balanceAmount = Math.max(0.0, quotedAmount - paidAmount);
            updateSummaryLabels();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load job financial details!").show();
        }
    }

    private double fetchQuotedAmount(int quotationId) {
        if (quotationId <= 0) {
            return 0.0;
        }
        try {
            String q = "SELECT total_amount FROM quotations WHERE quotation_id = ?";
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setInt(1, quotationId);
            ResultSet r = ps.executeQuery();
            double val = 0.0;
            if (r.next()) {
                val = r.getDouble("total_amount");
            }
            r.close();
            ps.close();
            return val;
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load quoted amount!").show();
            return 0.0;
        }
    }

    private void updateSummaryLabels() {
        quotedLbl.setText("Rs. " + CURRENCY_FMT.format(quotedAmount));
        paidLbl.setText("Rs. " + CURRENCY_FMT.format(paidAmount));
        balanceLbl.setText("Rs. " + CURRENCY_FMT.format(balanceAmount));
    }

    private void loadPaymentHistory(int jobId) {
        List<ClientPaymentTbl> rows = new ArrayList<>();

        try {
            conn = DBConnection.getInstance().getConnection();
            String query = "SELECT p.payment_date, p.payment_type, p.amount, p.method, "
                    + "COALESCE(u.username, CAST(p.received_by AS CHAR)) AS received_by_name "
                    + "FROM payments p "
                    + "LEFT JOIN users u ON p.received_by = u.user_id "
                    + "WHERE p.job_id = ? "
                    + "ORDER BY p.payment_date DESC, p.payment_id DESC";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, jobId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("payment_date");
                String dateStr = ts != null ? ts.toLocalDateTime().toLocalDate().format(DISPLAY_DATE_FMT) : "";
                String typeStr = rs.getString("payment_type");
                double amt = rs.getDouble("amount");
                String methodStr = rs.getString("method");
                String receivedBy = rs.getString("received_by_name");

                rows.add(new ClientPaymentTbl(
                        dateStr,
                        typeStr,
                        "Rs. " + CURRENCY_FMT.format(amt),
                        methodStr,
                        receivedBy != null ? receivedBy : "1"
                ));
            }

            rs.close();
            pstmt.close();
            paymentHistoryTbl.setItems(FXCollections.observableArrayList(rows));

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load payment history!").show();
        }
    }

    private int resolveReceivedByUserId() {
        try {
            String q = "SELECT user_id FROM users ORDER BY user_id ASC LIMIT 1";
            Statement st = conn.createStatement();
            ResultSet r = st.executeQuery(q);
            int id = 1;
            if (r.next()) {
                id = r.getInt("user_id");
            }
            r.close();
            st.close();
            return id;
        } catch (SQLException e) {
            return 1;
        }
    }

    @FXML
    void clickBtnSavePayment(ActionEvent event) {
        if (currentJob == null && jobCombo.getValue() != null) {
            applySelectedJob(jobCombo.getValue());
        }

        if (currentJob == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a job first!").show();
            return;
        }

        PaymentType paymentType = paymentTypeCombo.getSelectionModel().getSelectedItem();
        String amountText = amountTxtField.getText().trim();
        LocalDate paymentDate = paymentDatePicker.getValue();
        PaymentMethod method = methodCombo.getSelectionModel().getSelectedItem();

        if (paymentType == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a payment type!").show();
            return;
        }
        if (amountText.isEmpty() || !amountText.matches(MONEY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid amount!").show();
            return;
        }

        double amount = Double.parseDouble(amountText);
        if (amount <= 0) {
            new Alert(Alert.AlertType.ERROR, "Amount must be greater than zero!").show();
            return;
        }
        if (amount > balanceAmount + 0.005) {
            new Alert(Alert.AlertType.ERROR, "A payment cannot exceed the outstanding balance!").show();
            return;
        }
        if (paymentDate == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a payment date!").show();
            return;
        }
        if (method == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a payment method!").show();
            return;
        }

        ClientPayment payment = new ClientPayment();
        payment.setJobId(currentJob.jobId);
        payment.setPaymentType(paymentType);
        payment.setAmount(amount);
        payment.setPaymentDate(paymentDate);
        payment.setPaymentMethod(method);
        payment.setReceivedBy("1");

        saveClientPayment(payment);
    }

    private void saveClientPayment(ClientPayment payment) {
        try {
            conn = DBConnection.getInstance().getConnection();
            int receivedById = resolveReceivedByUserId();
            LocalDateTime dateTime = LocalDateTime.of(payment.getPaymentDate(), LocalTime.now());

            String insertQuery = "INSERT INTO payments("
                    + "job_id, received_by, payment_type, payment_date, amount, method) "
                    + "VALUES(?,?,?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(insertQuery);
            pstmt.setInt(1, payment.getJobId());
            pstmt.setInt(2, receivedById);
            pstmt.setString(3, payment.getPaymentType().name());
            pstmt.setTimestamp(4, Timestamp.valueOf(dateTime));
            pstmt.setDouble(5, payment.getAmount());
            pstmt.setString(6, payment.getPaymentMethod().getLabel());

            int affected = pstmt.executeUpdate();
            pstmt.close();

            if (affected > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Payment recorded successfully!").show();
                loadJobFinancialSummary(currentJob);
                loadPaymentHistory(currentJob.jobId);
                clearFormFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to record payment!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong while saving payment!").show();
        }
    }

    @FXML
    void clickBtnClear(ActionEvent event) {
        clearFormFields();
    }

    private void clearFormFields() {
        paymentTypeCombo.setValue(null);
        amountTxtField.setText("");
        paymentDatePicker.setValue(LocalDate.now());
        methodCombo.setValue(null);
    }

    private void resetSummaryAndHistory() {
        currentJob = null;
        quotedAmount = 0.0;
        paidAmount = 0.0;
        balanceAmount = 0.0;
        updateSummaryLabels();
        paymentHistoryTbl.setItems(FXCollections.observableArrayList());
    }

    @FXML
    void clickBtnGenerateInvoice(ActionEvent event) {
        if (currentJob == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a job first!").show();
            return;
        }
        if (!"COMPLETED".equalsIgnoreCase(currentJob.status)) {
            new Alert(Alert.AlertType.INFORMATION, "Invoice is created once the job is completed.").show();
            return;
        }
        new Alert(Alert.AlertType.INFORMATION, "Invoice generated for Job " + currentJob.jobNo + ".").show();
    }
}