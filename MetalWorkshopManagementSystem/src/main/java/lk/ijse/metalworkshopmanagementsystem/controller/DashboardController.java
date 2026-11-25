package lk.ijse.metalworkshopmanagementsystem.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import lk.ijse.metalworkshopmanagementsystem.db.DBConnection;

public class DashboardController implements Initializable {

    @FXML
    private AnchorPane dashboardPane;

    @FXML
    private Label overviewSubtitleLbl;

    @FXML
    private Label activeJobsLbl;

    @FXML
    private Label activeJobsHintLbl;

    @FXML
    private Label pendingQuotationsLbl;

    @FXML
    private Label pendingQuotationsHintLbl;

    @FXML
    private Label lowStockLbl;

    @FXML
    private Label lowStockHintLbl;

    @FXML
    private Label outstandingBalanceLbl;

    @FXML
    private Label outstandingBalanceHintLbl;

    @FXML
    private Label openInquiriesLbl;

    @FXML
    private Label completedJobsLbl;

    @FXML
    private Label availableOffcutsLbl;

    @FXML
    private Label activeWorkersLbl;

    @FXML
    private Label totalQuotedLbl;

    @FXML
    private Label totalCollectedLbl;

    @FXML
    private Label totalPendingLbl;

    @FXML
    private Label stockValueLbl;

    @FXML
    private Button newEstimateBtn;

    @FXML
    private Button newJobOrderBtn;

    @FXML
    private Button recordPaymentBtn;

    @FXML
    private Button viewStockBtn;

    @FXML
    private Button refreshBtn;

    private Connection conn;
    private static final DecimalFormat CURRENCY_FMT = new DecimalFormat("#,##0.00");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LocalDate now = LocalDate.now();
        String monthName = now.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        if (overviewSubtitleLbl != null) {
            overviewSubtitleLbl.setText("Business overview for " + monthName + " " + now.getYear());
        }
        loadDashboardMetrics();
    }

    @FXML
    void clickRefresh(ActionEvent event) {
        loadDashboardMetrics();
    }

    @FXML
    void clickNewEstimate(ActionEvent event) {
        navigateToView("Estimator.fxml", "Estimator");
    }

    @FXML
    void clickNewJobOrder(ActionEvent event) {
        navigateToView("JobOrders.fxml", "Job Orders");
    }

    @FXML
    void clickRecordPayment(ActionEvent event) {
        navigateToView("ClientPayments.fxml", "Client Payments");
    }

    @FXML
    void clickViewStock(ActionEvent event) {
        navigateToView("MaterialStock.fxml", "Material Stock");
    }

    private void navigateToView(String fxmlFile, String title) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(
                    getClass().getResource("/view/" + fxmlFile)
            );
            if (dashboardPane != null && dashboardPane.getParent() instanceof javafx.scene.layout.Pane) {
                javafx.scene.layout.Pane container = (javafx.scene.layout.Pane) dashboardPane.getParent();
                container.getChildren().setAll(root);
            } else if (dashboardPane != null) {
                dashboardPane.getChildren().setAll(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.INFORMATION, "Open " + title + " from the left navigation menu.").show();
        }
    }

    private void loadDashboardMetrics() {
        try {
            conn = DBConnection.getInstance().getConnection();

            int activeJobs = queryInt(
                    "SELECT COUNT(*) FROM job_orders WHERE UPPER(COALESCE(status, '')) NOT IN ('COMPLETED', 'CANCELLED', 'CLOSED')"
            );
            int completedJobs = queryInt(
                    "SELECT COUNT(*) FROM job_orders WHERE UPPER(COALESCE(status, '')) = 'COMPLETED'"
            );
            int pendingQuotations = queryInt(
                    "SELECT COUNT(*) FROM quotations WHERE UPPER(COALESCE(status, '')) IN ('PENDING', 'DRAFT', 'SENT')"
            );
            int lowStockItems = queryInt(
                    "SELECT COUNT(*) FROM materials WHERE COALESCE(is_active, 1) = 1 AND quantity_on_hand <= reorder_level"
            );
            int openInquiries = queryInt(
                    "SELECT COUNT(*) FROM inquiries WHERE UPPER(COALESCE(status, '')) NOT IN ('CLOSED', 'CONVERTED', 'CANCELLED')"
            );
            int availableOffcuts = queryInt(
                    "SELECT COUNT(*) FROM off_cuts WHERE UPPER(COALESCE(status, '')) = 'AVAILABLE'"
            );
            int activeWorkers = queryInt(
                    "SELECT COUNT(*) FROM workers WHERE COALESCE(is_active, 1) = 1"
            );

            double totalQuoted = queryDouble(
                    "SELECT COALESCE(SUM(q.total_amount), 0) "
                    + "FROM job_orders jo "
                    + "JOIN quotations q ON jo.quotation_id = q.quotation_id "
                    + "WHERE UPPER(COALESCE(jo.status, '')) <> 'CANCELLED'"
            );
            if (totalQuoted == 0.0) {
                totalQuoted = queryDouble("SELECT COALESCE(SUM(total_amount), 0) FROM quotations");
            }

            double totalCollected = queryDouble("SELECT COALESCE(SUM(amount), 0) FROM payments");
            double totalPending = Math.max(0.0, totalQuoted - totalCollected);
            double stockValue = queryDouble(
                    "SELECT COALESCE(SUM(quantity_on_hand * unit_cost), 0) "
                    + "FROM materials WHERE COALESCE(is_active, 1) = 1"
            );

            activeJobsLbl.setText(String.valueOf(activeJobs));
            completedJobsLbl.setText(String.valueOf(completedJobs));
            pendingQuotationsLbl.setText(String.valueOf(pendingQuotations));
            lowStockLbl.setText(String.valueOf(lowStockItems));
            openInquiriesLbl.setText(String.valueOf(openInquiries));
            availableOffcutsLbl.setText(String.valueOf(availableOffcuts));
            activeWorkersLbl.setText(String.valueOf(activeWorkers));

            String formattedPending = "Rs. " + CURRENCY_FMT.format(totalPending);
            outstandingBalanceLbl.setText(formattedPending);
            totalQuotedLbl.setText("Rs. " + CURRENCY_FMT.format(totalQuoted));
            totalCollectedLbl.setText("Rs. " + CURRENCY_FMT.format(totalCollected));
            totalPendingLbl.setText(formattedPending);
            stockValueLbl.setText("Rs. " + CURRENCY_FMT.format(stockValue));

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load dashboard metrics!").show();
        }
    }

    private int queryInt(String sql) {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ignored) {
            // Keep 0 if a table or column has a different name in a test DB
        }
        return 0;
    }

    private double queryDouble(String sql) {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException ignored) {
            // Keep 0.0 if a table or column has a different name in a test DB
        }
        return 0.0;
    }
}