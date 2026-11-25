package lk.ijse.metalworkshopmanagementsystem.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.metalworkshopmanagementsystem.db.DBConnection;
import lk.ijse.metalworkshopmanagementsystem.model.JobStatus;
import lk.ijse.metalworkshopmanagementsystem.model.tbl.JobOrderTbl;

public class JobOrdersController implements Initializable {

    @FXML
    private Button clearBtn;

    @FXML
    private TextField clientNameTxtField;

    @FXML
    private TableColumn<JobOrderTbl, String> colClient;

    @FXML
    private TableColumn<JobOrderTbl, String> colDue;

    @FXML
    private TableColumn<JobOrderTbl, String> colJobNo;

    @FXML
    private TableColumn<JobOrderTbl, String> colStatus;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private AnchorPane jobOrdersPane;

    @FXML
    private TableView<JobOrderTbl> jobTbl;

    @FXML
    private ComboBox<String> quotationCombo;

    @FXML
    private Button saveBtn;

    @FXML
    private Button searchBtn;

    @FXML
    private TextField searchTxtField;

    @FXML
    private TextField siteAddressTxtField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private ComboBox<JobStatus> statusCombo;

    @FXML
    private Button updateBtn;

    Connection conn;

    private int selectedJobId = 0;

    private static final String ADDRESS_REGEX = "^[A-Za-z0-9/,.#\\-\\s]{3,100}$";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colJobNo.setCellValueFactory(new PropertyValueFactory<>("jobNo"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        colDue.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        statusCombo.setItems(FXCollections.observableArrayList(JobStatus.values()));
        statusCombo.setValue(JobStatus.PENDING);
        startDatePicker.setValue(LocalDate.now());

        quotationCombo.setOnAction(e -> showQuotationDetails());
        jobTbl.setOnMouseClicked(e -> {
            JobOrderTbl row = jobTbl.getSelectionModel().getSelectedItem();
            if (row != null) {
                fillFields(row.getJobNo());
            }
        });

        loadQuotationCombo();
        loadJobOrdersTable();
    }

    private int parseNumericId(String raw) {
        if (raw == null) {
            return -1;
        }
        String firstToken = raw.trim().split("\\s+")[0];
        String digits = firstToken.replaceAll("\\D+", "");
        if (digits.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void showQuotationDetails() {
        int quotationId = getSelectedQuotationId();
        if (quotationId <= 0) {
            return;
        }
        try {
            conn = DBConnection.getInstance().getConnection();
            String query = "SELECT c.full_name, i.site_location FROM quotations q "
                    + "JOIN estimates e ON q.estimate_id = e.estimate_id "
                    + "JOIN inquiries i ON e.inquiry_id = i.inquiry_id "
                    + "JOIN clients c ON i.client_id = c.client_id "
                    + "WHERE q.quotation_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, quotationId);
            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                clientNameTxtField.setText(result.getString("full_name"));
                siteAddressTxtField.setText(result.getString("site_location"));
            }
            result.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    // ------------------------------- Load Combo ------------------------------

    private void loadQuotationCombo() {
        ObservableList<String> quotationlist = FXCollections.observableArrayList();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT q.quotation_id, q.quotation_no, c.full_name FROM quotations q "
                    + "JOIN estimates e ON q.estimate_id = e.estimate_id "
                    + "JOIN inquiries i ON e.inquiry_id = i.inquiry_id "
                    + "JOIN clients c ON i.client_id = c.client_id "
                    + "WHERE q.quotation_id NOT IN (SELECT quotation_id FROM job_orders) "
                    + "AND q.status NOT IN ('REJECTED','EXPIRED') "
                    + "ORDER BY q.quotation_id DESC";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String id = String.format("QU%03d", result.getInt("quotation_id"));
                String name = result.getString("full_name");

                quotationlist.add(id + "  " + name);
            }

            quotationCombo.setItems(quotationlist);

            result.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    // ---------------------------- Clean Fields -------------------------------

    @FXML
    void clickBtnClear(ActionEvent event) {
        cleanFields();
    }

    private void cleanFields() {
        selectedJobId = 0;
        quotationCombo.setDisable(false);
        quotationCombo.setValue(null);
        loadQuotationCombo();
        searchTxtField.setText("");
        clientNameTxtField.setText("");
        siteAddressTxtField.setText("");
        startDatePicker.setValue(LocalDate.now());
        dueDatePicker.setValue(null);
        statusCombo.setValue(JobStatus.PENDING);
        jobTbl.getSelectionModel().clearSelection();
    }

    // ------------------------------ Validation -------------------------------

    private boolean isValid() {
        if (!siteAddressTxtField.getText().trim().matches(ADDRESS_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid site address").show();
            return false;
        }
        if (startDatePicker.getValue() == null || dueDatePicker.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "Please select the start and due dates").show();
            return false;
        }
        if (dueDatePicker.getValue().isBefore(startDatePicker.getValue())) {
            new Alert(Alert.AlertType.ERROR, "Due date cannot be before the start date").show();
            return false;
        }
        if (statusCombo.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a status").show();
            return false;
        }
        return true;
    }

    // ---------------------------- Save Job Order -----------------------------

    @FXML
    void clickBtnSave(ActionEvent event) {
        if (selectedJobId != 0) {
            new Alert(Alert.AlertType.ERROR,
                    "A job is loaded. Click Clear to create a new one, or Update to change it").show();
            return;
        }
        if (quotationCombo.getValue() == null || getSelectedQuotationId() <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please select a quotation").show();
            return;
        }
        if (!isValid()) {
            return;
        }

        addJob();
    }

    private void addJob() {
        try {
            conn = DBConnection.getInstance().getConnection();

            Statement stmt = conn.createStatement();
            ResultSet r = stmt.executeQuery("SELECT IFNULL(MAX(job_id), 0) + 1 AS next_id FROM job_orders");
            int nextId = r.next() ? r.getInt("next_id") : 1;
            r.close();
            stmt.close();

            String jobNo = String.format("JOB-%04d", nextId);

            String query = "INSERT INTO job_orders("
                    + "quotation_id, job_no, site_address, start_date, due_date, completed_date, status) "
                    + "VALUES(?,?,?,?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(query);
            int quotationId = getSelectedQuotationId();

            pstmt.setInt(1, quotationId);
            pstmt.setString(2, jobNo);
            pstmt.setString(3, siteAddressTxtField.getText().trim());
            pstmt.setObject(4, startDatePicker.getValue());
            pstmt.setObject(5, dueDatePicker.getValue());
            pstmt.setObject(6, statusCombo.getValue() == JobStatus.COMPLETED ? LocalDate.now() : null);
            pstmt.setString(7, statusCombo.getValue().name());

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                PreparedStatement p2 = conn.prepareStatement("UPDATE quotations "
                        + "SET status='ACCEPTED' WHERE quotation_id=?");
                p2.setInt(1, quotationId);
                p2.executeUpdate();
                p2.close();

                new Alert(Alert.AlertType.INFORMATION, "Job " + jobNo + " saved successfully!").show();

                loadJobOrdersTable();
                cleanFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    private int getSelectedQuotationId() {
        return parseNumericId(quotationCombo.getValue());
    }

    // --------------------------- Update Job Order -----------------------------

    @FXML
    void clickBtnUpdate(ActionEvent event) {
        if (selectedJobId == 0) {
            new Alert(Alert.AlertType.ERROR,
                    "Please search or click a job in the table first").show();
            return;
        }
        if (!isValid()) {
            return;
        }

        update();
    }

    private void update() {
        try {
            conn = DBConnection.getInstance().getConnection();
            String query = "UPDATE job_orders SET site_address=?, start_date=?, due_date=?, "
                    + "completed_date=?, status=? WHERE job_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, siteAddressTxtField.getText().trim());
            pstmt.setObject(2, startDatePicker.getValue());
            pstmt.setObject(3, dueDatePicker.getValue());
            pstmt.setObject(4, statusCombo.getValue() == JobStatus.COMPLETED ? LocalDate.now() : null);
            pstmt.setString(5, statusCombo.getValue().name());
            pstmt.setInt(6, selectedJobId);

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Job updated successfully!").show();
                loadJobOrdersTable();
                cleanFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    // -------------------------- Search Job orders ----------------------------

    @FXML
    void pressSearchJob(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            fillFields(searchTxtField.getText().trim());
        }
    }

    @FXML
    void clickBtnSearch(ActionEvent event) {
        fillFields(searchTxtField.getText().trim());
    }

    private void fillFields(String jobNo) {
        if (jobNo == null || jobNo.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a Job No (e.g., JOB-0001)").show();
            return;
        }

        try {
            conn = DBConnection.getInstance().getConnection();
            String query = "SELECT j.job_id, j.site_address, j.start_date, j.due_date, j.status, c.full_name "
                    + "FROM job_orders j "
                    + "JOIN quotations q ON j.quotation_id = q.quotation_id "
                    + "JOIN estimates e ON q.estimate_id = e.estimate_id "
                    + "JOIN inquiries i ON e.inquiry_id = i.inquiry_id "
                    + "JOIN clients c ON i.client_id = c.client_id "
                    + "WHERE j.job_no = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, jobNo);
            ResultSet result = pstmt.executeQuery();

            if (result.next()) {
                selectedJobId = result.getInt("job_id");
                quotationCombo.setValue(null);
                quotationCombo.setDisable(true);
                clientNameTxtField.setText(result.getString("full_name"));
                siteAddressTxtField.setText(result.getString("site_address"));
                String startStr = result.getString("start_date");
                if (startStr != null && !startStr.isEmpty()) {
                    startDatePicker.setValue(LocalDate.parse(startStr));
                }
                String dueStr = result.getString("due_date");
                if (dueStr != null && !dueStr.isEmpty()) {
                    dueDatePicker.setValue(LocalDate.parse(dueStr));
                }
                statusCombo.setValue(JobStatus.fromDb(result.getString("status")));
            } else {
                new Alert(Alert.AlertType.ERROR, "Job not found!").show();
            }
            result.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    // ---------------------- Load Job Orders Table ----------------------------

    private void loadJobOrdersTable() {
        List<JobOrderTbl> jobOrderTblList = getAllJobOrders();

        ObservableList<JobOrderTbl> obList
                = FXCollections.observableArrayList(jobOrderTblList);

        jobTbl.setItems(obList);
    }

    private List<JobOrderTbl> getAllJobOrders() {
        List<JobOrderTbl> jobOrderList = new ArrayList<>();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT j.job_no, c.full_name, j.due_date, j.status FROM job_orders j "
                    + "JOIN quotations q ON j.quotation_id = q.quotation_id "
                    + "JOIN estimates e ON q.estimate_id = e.estimate_id "
                    + "JOIN inquiries i ON e.inquiry_id = i.inquiry_id "
                    + "JOIN clients c ON i.client_id = c.client_id "
                    + "ORDER BY j.job_id DESC";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                JobOrderTbl jobOrder = new JobOrderTbl();
                jobOrder.setJobNo(result.getString("job_no"));
                jobOrder.setClientName(result.getString("full_name"));
                String dueStr = result.getString("due_date");
                if (dueStr != null && !dueStr.isEmpty()) {
                    jobOrder.setDueDate(LocalDate.parse(dueStr));
                }
                jobOrder.setStatus(result.getString("status"));

                jobOrderList.add(jobOrder);
            }

            result.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return jobOrderList;
    }
}