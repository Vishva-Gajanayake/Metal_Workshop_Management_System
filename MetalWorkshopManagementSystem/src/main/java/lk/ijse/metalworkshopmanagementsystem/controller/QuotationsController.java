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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.metalworkshopmanagementsystem.db.DBConnection;
import lk.ijse.metalworkshopmanagementsystem.model.Quotation;
import lk.ijse.metalworkshopmanagementsystem.model.QuotationStatus;
import lk.ijse.metalworkshopmanagementsystem.model.tbl.QuotationTbl;

public class QuotationsController implements Initializable {

    Connection conn;

    @FXML
    private TextField advanceTxtField;

    @FXML
    private TextField discountTxtField;

    @FXML
    private ComboBox<String> estimateCombo;

    @FXML
    private TextField profitTxtField;

    @FXML
    private DatePicker quotationDatePicker;

    @FXML
    private ComboBox<QuotationStatus> statusCombo;

    @FXML
    private DatePicker validUntilDatePicker;

    @FXML
    private AnchorPane quotationPane;

    @FXML
    private TableColumn<QuotationTbl, String> colClient;

    @FXML
    private TableColumn<QuotationTbl, String> colNo;

    @FXML
    private TableColumn<QuotationTbl, String> colStatus;

    @FXML
    private TableColumn<QuotationTbl, Double> colTotal;

    @FXML
    private TableColumn<QuotationTbl, LocalDate> colValidDate;

    @FXML
    private TableView<QuotationTbl> quoteTbl;

    @FXML
    private TextField searchTxtField;

    @FXML
    private Label clientNameLbl;

    @FXML
    private Label quoteNoLbl;

    @FXML
    private Label totalLbl;

    private int quoteNoCounter = 1;

    QuotationTbl selectedQuotation = null;

    private static final String MONEY_REGEX = "^\\d{1,8}(\\.\\d{1,2})?$";
    private static final String PERCENT_REGEX = "^(100(\\.0{1,2})?|\\d{1,2}(\\.\\d{1,2})?)$";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        syncQuoteCounterFromDb();
        totalLbl.setText("RS.0.00");
        quoteNoLbl.setText(String.format("QUO-%04d", quoteNoCounter));
        clientNameLbl.setText("Client Name");

        colClient.setCellValueFactory(new PropertyValueFactory<>("client"));
        colNo.setCellValueFactory(new PropertyValueFactory<>("quotationNo"));
        colValidDate.setCellValueFactory(new PropertyValueFactory<>("validUntil"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        estimateCombo.setOnAction(event -> {
            String selectedEst = estimateCombo.getSelectionModel().getSelectedItem();
            int estId = parseNumericId(selectedEst);
            if (estId > 0) {
                String cName = getClientNameByEstimateId(estId);
                clientNameLbl.setText(cName != null ? cName : "Client Name");
            }
        });

        quoteTbl.setOnMouseClicked(event -> {
            QuotationTbl selected = quoteTbl.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedQuotation = selected;
                searchTxtField.setText(selected.getQuotationNo());
                fillFields(selected.getQuotationNo());
            }
        });

        loadEstimateComboBox();
        loadStatusCombo();
        loadQuotationTable();
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

    private void syncQuoteCounterFromDb() {
        try {
            conn = DBConnection.getInstance().getConnection();
            String query = "SELECT COALESCE(MAX(quotation_id), 0) AS max_id FROM quotations";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                quoteNoCounter = Math.max(quoteNoCounter, rs.getInt("max_id") + 1);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadStatusCombo() {
        statusCombo.setItems(FXCollections.observableArrayList(QuotationStatus.values()));
    }

    private void loadEstimateComboBox() {
        List<String> estimateList = new ArrayList<>();
        ObservableList<String> estimateObList = FXCollections.observableArrayList();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM estimates ORDER BY estimate_id ASC";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String id = String.format("E%03d", result.getInt("estimate_id"));
                estimateList.add(id);
            }

            estimateObList.addAll(estimateList);
            estimateCombo.setItems(estimateObList);

            result.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    @FXML
    void clickCalculateBtn(ActionEvent event) {
        String estimate = estimateCombo.getSelectionModel().getSelectedItem();
        int estId = parseNumericId(estimate);
        String margin = profitTxtField.getText().trim();
        String discount = discountTxtField.getText().trim();

        if (estimate == null || estId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please select an Estimate").show();
        } else if (!margin.matches(PERCENT_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Margin Percentage").show();
        } else if (!discount.matches(MONEY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Discount").show();
        } else {
            double total = getEstimateCost(estId)
                    * (1 + Double.parseDouble(margin) / 100.0)
                    - Double.parseDouble(discount);

            totalLbl.setText(String.format("RS.%.2f", total));
        }
    }

    @FXML
    void clickSaveBtn(ActionEvent event) {
        quoteNoLbl.setText(String.format("QUO-%04d", quoteNoCounter));
        String estimate = estimateCombo.getSelectionModel().getSelectedItem();
        int estId = parseNumericId(estimate);
        String margin = profitTxtField.getText().trim();
        String discount = discountTxtField.getText().trim();
        LocalDate quoteDate = quotationDatePicker.getValue();
        LocalDate valiDate = validUntilDatePicker.getValue();
        String advance = advanceTxtField.getText().trim();
        QuotationStatus selectedStatus = statusCombo.getValue();
        String status = (selectedStatus != null) ? selectedStatus.name() : QuotationStatus.DRAFT.name();

        if (estimate == null || estId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please select an Estimate").show();
        } else if (!margin.matches(PERCENT_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Margin Percentage").show();
        } else if (!discount.matches(MONEY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Discount").show();
        } else if (!advance.matches(MONEY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Advance Amount").show();
        } else if (quoteDate == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a quotation date").show();
        } else if (valiDate == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a valid date").show();
        } else {
            Quotation q = new Quotation();
            q.setEstimateId(estId);
            q.setQuotationNo(quoteNoLbl.getText());
            q.setQuotationDate(quoteDate);
            q.setValidUntil(valiDate);
            q.setMarginPct(Double.parseDouble(margin));
            q.setDiscountAmount(Double.parseDouble(discount));

            double total = getEstimateCost(estId)
                    * (1 + Double.parseDouble(margin) / 100.0)
                    - Double.parseDouble(discount);
            q.setTotalAmount(total);
            totalLbl.setText(String.format("RS.%.2f", total));

            q.setAdvanceRequired(Double.parseDouble(advance));
            q.setStatus(toStatus(status));

            if (addQuotation(q)) {
                quoteNoCounter++;
                quoteNoLbl.setText(String.format("QUO-%04d", quoteNoCounter));
            }
        }
    }

    @FXML
    void clickMarkAccepted(ActionEvent event) {
        selectedQuotation = quoteTbl.getSelectionModel().getSelectedItem();

        if (selectedQuotation == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a quotation from the table first!").show();
            return;
        }

        updateStatus(selectedQuotation.getQuotationNo(), QuotationStatus.ACCEPTED.name());
    }

    @FXML
    void clickMarkReject(ActionEvent event) {
        selectedQuotation = quoteTbl.getSelectionModel().getSelectedItem();

        if (selectedQuotation == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a quotation from the table first!").show();
            return;
        }

        updateStatus(selectedQuotation.getQuotationNo(), QuotationStatus.REJECTED.name());
    }

    private void updateStatus(String quotationNo, String status) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "UPDATE quotations SET status=? WHERE quotation_no=?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, status);
            pstmt.setString(2, quotationNo);

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Quotation status updated successfully!").show();
                loadQuotationTable();
            } else {
                new Alert(Alert.AlertType.ERROR, "Quotation not found or not updated!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    private double getEstimateCost(int intId) {
        double cost = 0.0;

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT total_cost FROM estimates WHERE estimate_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, intId);

            ResultSet result = pstmt.executeQuery();

            if (result.next()) {
                cost = result.getDouble("total_cost");
            } else {
                new Alert(Alert.AlertType.ERROR, "Estimate not found!").show();
            }

            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return cost;
    }

    private boolean addQuotation(Quotation q) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String queryInsert = "INSERT INTO quotations("
                    + "estimate_id, "
                    + "quotation_no, "
                    + "quotation_date, "
                    + "valid_until, "
                    + "margin_pct, "
                    + "discount_amount, "
                    + "total_amount, "
                    + "advance_required, "
                    + "status) "
                    + "VALUES(?,?,?,?,?,?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(queryInsert);

            pstmt.setInt(1, q.getEstimateId());
            pstmt.setString(2, q.getQuotationNo());
            pstmt.setObject(3, q.getQuotationDate());
            pstmt.setObject(4, q.getValidUntil());
            pstmt.setDouble(5, q.getMarginPct());
            pstmt.setDouble(6, q.getDiscountAmount());
            pstmt.setDouble(7, q.getTotalAmount());
            pstmt.setDouble(8, q.getAdvanceRequired());
            pstmt.setString(9, q.getStatus().name());

            int result1 = pstmt.executeUpdate();
            pstmt.close();

            if (result1 > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Quotation saved successfully!").show();
                loadQuotationTable();
                cleanQuotationfields();
                return true;
            } else {
                new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
            return false;
        }
    }

    // ---------------------- Load Quotation Table -----------------------------

    private void loadQuotationTable() {
        List<QuotationTbl> quotationTblList = getAllQuotations();

        ObservableList<QuotationTbl> obList
                = FXCollections.observableArrayList(quotationTblList);

        quoteTbl.setItems(obList);
    }

    private List<QuotationTbl> getAllQuotations() {
        List<QuotationTbl> quotationList = new ArrayList<>();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT q.quotation_id, q.quotation_no, c.full_name AS client_name, "
                    + "q.total_amount, q.valid_until, q.status "
                    + "FROM quotations q "
                    + "JOIN estimates e ON q.estimate_id = e.estimate_id "
                    + "JOIN inquiries i ON e.inquiry_id = i.inquiry_id "
                    + "JOIN clients c ON i.client_id = c.client_id "
                    + "ORDER BY q.quotation_id DESC";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                QuotationTbl q = new QuotationTbl();
                q.setQuotationNo(result.getString("quotation_no"));
                q.setClient(result.getString("client_name"));
                q.setTotalAmount(result.getDouble("total_amount"));
                String validUntilStr = result.getString("valid_until");
                if (validUntilStr != null && !validUntilStr.isEmpty()) {
                    q.setValidUntil(LocalDate.parse(validUntilStr));
                }
                q.setStatus(toStatus(result.getString("status")));

                quotationList.add(q);
            }

            result.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return quotationList;
    }

    private QuotationStatus toStatus(String text) {
        try {
            return QuotationStatus.valueOf(text);
        } catch (IllegalArgumentException | NullPointerException e) {
            return QuotationStatus.DRAFT;
        }
    }

    // ----------------------- Search Quotations -------------------------------

    @FXML
    void pressedSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String queryText = searchTxtField.getText().trim();
            if (queryText.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please enter a Quotation No (e.g., QUO-0001)").show();
                return;
            }
            fillFields(queryText);
        }
    }

    private void fillFields(String id) {
        Quotation q = searchQuotations(id);

        if (q != null) {
            quoteNoLbl.setText(q.getQuotationNo());
            String cName = getClientName(q.getQuotationId());
            clientNameLbl.setText(cName != null ? cName : "Client Name");

            String estCode = String.format("E%03d", q.getEstimateId());
            if (!estimateCombo.getItems().contains(estCode)) {
                estimateCombo.getItems().add(estCode);
            }
            estimateCombo.setValue(estCode);

            profitTxtField.setText(String.valueOf(q.getMarginPct()));
            discountTxtField.setText(String.valueOf(q.getDiscountAmount()));
            advanceTxtField.setText(String.valueOf(q.getAdvanceRequired()));
            statusCombo.setValue(q.getStatus());
            totalLbl.setText(String.format("RS.%.2f", q.getTotalAmount()));
            quotationDatePicker.setValue(q.getQuotationDate());
            validUntilDatePicker.setValue(q.getValidUntil());
        }
    }

    private Quotation searchQuotations(String id) {
        Quotation q = null;

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM quotations WHERE quotation_no=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, id);

            ResultSet result = pstmt.executeQuery();

            if (result.next()) {
                q = new Quotation();
                q.setQuotationId(result.getInt("quotation_id"));
                q.setEstimateId(result.getInt("estimate_id"));
                q.setQuotationNo(result.getString("quotation_no"));
                String qDate = result.getString("quotation_date");
                if (qDate != null && !qDate.isEmpty()) {
                    q.setQuotationDate(LocalDate.parse(qDate));
                }
                String vDate = result.getString("valid_until");
                if (vDate != null && !vDate.isEmpty()) {
                    q.setValidUntil(LocalDate.parse(vDate));
                }
                q.setMarginPct(result.getDouble("margin_pct"));
                q.setDiscountAmount(result.getDouble("discount_amount"));
                q.setTotalAmount(result.getDouble("total_amount"));
                q.setAdvanceRequired(result.getDouble("advance_required"));
                q.setStatus(toStatus(result.getString("status")));
            } else {
                new Alert(Alert.AlertType.ERROR, "Quotation not found!").show();
            }

            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return q;
    }

    // ----------------------- Get Client By Quotation / Estimate ID ----------------------

    private String getClientName(int id) {
        String clientName = null;

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT c.full_name AS client_name "
                    + "FROM quotations q "
                    + "JOIN estimates e ON q.estimate_id = e.estimate_id "
                    + "JOIN inquiries i ON e.inquiry_id = i.inquiry_id "
                    + "JOIN clients c ON i.client_id = c.client_id "
                    + "WHERE q.quotation_id = ?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                clientName = result.getString("client_name");
            }
            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return clientName;
    }

    private String getClientNameByEstimateId(int estimateId) {
        String clientName = null;

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT c.full_name AS client_name "
                    + "FROM estimates e "
                    + "JOIN inquiries i ON e.inquiry_id = i.inquiry_id "
                    + "JOIN clients c ON i.client_id = c.client_id "
                    + "WHERE e.estimate_id = ?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, estimateId);

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                clientName = result.getString("client_name");
            }
            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientName;
    }

    @FXML
    void clickCleanBtn(ActionEvent event) {
        cleanQuotationfields();
    }

    private void cleanQuotationfields() {
        searchTxtField.setText("");
        statusCombo.setValue(null);
        quotationDatePicker.setValue(null);
        profitTxtField.setText("");
        advanceTxtField.setText("");
        discountTxtField.setText("");
        estimateCombo.setValue(null);
        validUntilDatePicker.setValue(null);
        clientNameLbl.setText("Client Name");
        totalLbl.setText("RS.0.00");
        selectedQuotation = null;
        quoteTbl.getSelectionModel().clearSelection();

        quoteNoLbl.setText(String.format("QUO-%04d", quoteNoCounter));
    }
}