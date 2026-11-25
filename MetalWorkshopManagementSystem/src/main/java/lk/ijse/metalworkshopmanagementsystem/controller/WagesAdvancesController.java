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
import lk.ijse.metalworkshopmanagementsystem.model.WageAdvances;
import lk.ijse.metalworkshopmanagementsystem.model.tbl.WageAdvancesTbl;

public class WagesAdvancesController implements Initializable {

    @FXML
    private AnchorPane wageAdvancesPane;

    @FXML
    private DatePicker advanceDatePicker;

    @FXML
    private TextField amountTxtField;

    @FXML
    private TextField reasonTxtField;

    @FXML
    private TextField searchTxtField;

    @FXML
    private Button saveBtn;

    @FXML
    private Button searchBtn;

    @FXML
    private TableView<WageAdvancesTbl> wageAdvancesTbl;

    @FXML
    private TableColumn<WageAdvancesTbl, Double> colAmount;

    @FXML
    private TableColumn<WageAdvancesTbl, LocalDate> colDate;

    @FXML
    private TableColumn<WageAdvancesTbl, String> colId;

    @FXML
    private TableColumn<WageAdvancesTbl, String> colStatus;

    @FXML
    private TableColumn<WageAdvancesTbl, String> colWorker;

    @FXML
    private ComboBox<String> workerCombo;

    Connection conn;

    private static final String MONEY_REGEX = "^\\d{1,8}(\\.\\d{1,2})?$";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("advancesId"));
        colWorker.setCellValueFactory(new PropertyValueFactory<>("worker"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("advanceDate"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        wageAdvancesTbl.setOnMouseClicked(event -> {
            WageAdvancesTbl selected = wageAdvancesTbl.getSelectionModel().getSelectedItem();
            if (selected != null) {
                searchTxtField.setText(selected.getAdvancesId());
                fillFields(selected.getAdvancesId());
            }
        });

        loadWorkersComboBox();
        loadAdvancesTable();
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

    private String findWorkerComboItem(String workerCodeOrId) {
        int targetId = parseNumericId(workerCodeOrId);
        if (targetId <= 0 || workerCombo.getItems() == null) {
            return workerCodeOrId;
        }
        for (String item : workerCombo.getItems()) {
            if (parseNumericId(item) == targetId) {
                return item;
            }
        }
        return workerCodeOrId;
    }

    private void loadWorkersComboBox() {
        List<String> workerList = new ArrayList<>();
        ObservableList<String> workerObList = FXCollections.observableArrayList();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM workers ORDER BY worker_id ASC";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String id = String.format("W%03d", result.getInt("worker_id"));
                String name = result.getString("full_name");
                workerList.add(id + "  " + name);
            }

            workerObList.addAll(workerList);
            workerCombo.setItems(workerObList);

            result.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    @FXML
    void clickBtnSave(ActionEvent event) {
        String worker = workerCombo.getSelectionModel().getSelectedItem();
        LocalDate date = advanceDatePicker.getValue();
        String amount = amountTxtField.getText().trim();
        String reason = reasonTxtField.getText().trim();

        int workerId = parseNumericId(worker);

        if (worker == null || workerId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please select a worker").show();
        } else if (!amount.matches(MONEY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid amount").show();
        } else if (date == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a date").show();
        } else {
            WageAdvances advance = new WageAdvances();
            advance.setWorkerId(String.valueOf(workerId));
            advance.setAdvanceDate(date);
            advance.setAmount(Double.parseDouble(amount));
            advance.setReason(reason);

            addAdvances(advance, workerId);
        }
    }

    private void addAdvances(WageAdvances advanceModel, int workerId) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "INSERT INTO worker_advances("
                    + "worker_id, "
                    + "advance_date, "
                    + "amount, "
                    + "reason) "
                    + "VALUES(?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, workerId);
            pstmt.setObject(2, advanceModel.getAdvanceDate());
            pstmt.setDouble(3, advanceModel.getAmount());
            pstmt.setString(4, advanceModel.getReason());

            int result1 = pstmt.executeUpdate();
            pstmt.close();

            if (result1 > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Wage Advance saved successfully!").show();
                loadAdvancesTable();
                cleanAdvanceFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Something went wrong").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    private void loadAdvancesTable() {
        List<WageAdvances> advancesList = getAllAdvances();
        List<WageAdvancesTbl> advancesTblList = new ArrayList<>();

        for (WageAdvances advance : advancesList) {
            WageAdvancesTbl advanceTbl = new WageAdvancesTbl();
            advanceTbl.setAdvancesId(advance.getAdvancesId());
            advanceTbl.setWorker(advance.getWorkerId());
            advanceTbl.setAdvanceDate(advance.getAdvanceDate());
            advanceTbl.setAmount(advance.getAmount());

            advancesTblList.add(advanceTbl);
        }

        ObservableList<WageAdvancesTbl> obList
                = FXCollections.observableArrayList(advancesTblList);

        wageAdvancesTbl.setItems(obList);
    }

    private List<WageAdvances> getAllAdvances() {
        List<WageAdvances> advanceList = new ArrayList<>();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM worker_advances ORDER BY advance_id DESC";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                WageAdvances advance = new WageAdvances();
                advance.setAdvancesId(String.format("AD%03d", result.getInt("advance_id")));
                advance.setWorkerId(String.format("W%03d", result.getInt("worker_id")));
                advance.setAdvanceDate(LocalDate.parse(result.getString("advance_date")));
                advance.setAmount(result.getDouble("amount"));

                advanceList.add(advance);
            }

            result.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return advanceList;
    }

    @FXML
    void clickBtnSearch(ActionEvent event) {
        performSearch();
    }

    @FXML
    void pressSearchWageAdvances(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            performSearch();
        }
    }

    private void performSearch() {
        int advanceId = parseNumericId(searchTxtField.getText());
        if (advanceId <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please enter a valid Advance ID (e.g., AD001 or 1)").show();
            return;
        }
        fillFields(String.valueOf(advanceId));
    }

    private void fillFields(String id) {
        WageAdvances advance = searchAdvances(id);

        if (advance != null) {
            workerCombo.setValue(findWorkerComboItem(advance.getWorkerId()));
            advanceDatePicker.setValue(advance.getAdvanceDate());
            amountTxtField.setText(String.valueOf(advance.getAmount()));
            reasonTxtField.setText(advance.getReason());
        }
    }

    private WageAdvances searchAdvances(String id) {
        WageAdvances advance = null;
        int numId = parseNumericId(id);

        if (numId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Invalid Advance ID!").show();
            return null;
        }

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM worker_advances WHERE advance_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, numId);

            ResultSet result = pstmt.executeQuery();

            if (result.next()) {
                advance = new WageAdvances();
                advance.setWorkerId(String.format("W%03d", result.getInt("worker_id")));
                advance.setAdvanceDate(LocalDate.parse(result.getString("advance_date")));
                advance.setAmount(result.getDouble("amount"));
                advance.setReason(result.getString("reason"));
            } else {
                new Alert(Alert.AlertType.ERROR, "Wage Advance not found!").show();
            }

            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return advance;
    }

    private void cleanAdvanceFields() {
        searchTxtField.setText("");
        workerCombo.setValue(null);
        amountTxtField.setText("");
        reasonTxtField.setText("");
        advanceDatePicker.setValue(null);
        wageAdvancesTbl.getSelectionModel().clearSelection();
    }

    @FXML
    void clickBtnClean(ActionEvent event) {
        cleanAdvanceFields();
    }

    @FXML
    void clickBtnUpdate(ActionEvent event) {
        String advanceIdRaw = searchTxtField.getText().trim();
        int numericAdvanceId = parseNumericId(advanceIdRaw);

        String worker = workerCombo.getSelectionModel().getSelectedItem();
        int numericWorkerId = parseNumericId(worker);

        LocalDate date = advanceDatePicker.getValue();
        String amount = amountTxtField.getText().trim();
        String reason = reasonTxtField.getText().trim();

        if (advanceIdRaw.isEmpty() || numericAdvanceId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid Advance ID on search field").show();
        } else if (worker == null || numericWorkerId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please select a worker").show();
        } else if (!amount.matches(MONEY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid amount").show();
        } else if (date == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a date").show();
        } else {
            WageAdvances advance = new WageAdvances();
            advance.setAdvancesId(String.valueOf(numericAdvanceId));
            advance.setWorkerId(String.valueOf(numericWorkerId));
            advance.setAdvanceDate(date);
            advance.setAmount(Double.parseDouble(amount));
            advance.setReason(reason);

            updateAdvance(advance, numericWorkerId, numericAdvanceId);
        }
    }

    private void updateAdvance(WageAdvances advance, int numericWorkerId, int numericAdvanceId) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "UPDATE worker_advances SET "
                    + "worker_id=?, "
                    + "advance_date=?, "
                    + "amount=?, "
                    + "reason=? "
                    + "WHERE advance_id=?";

            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, numericWorkerId);
            pstmt.setObject(2, advance.getAdvanceDate());
            pstmt.setDouble(3, advance.getAmount());
            pstmt.setString(4, advance.getReason());
            pstmt.setInt(5, numericAdvanceId);

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Wage advance updated successfully!").show();
                loadAdvancesTable();
                cleanAdvanceFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Wage advance not found or not updated!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    @FXML
    void clickBtnDelete(ActionEvent event) {
        int advanceId = parseNumericId(searchTxtField.getText());
        if (advanceId <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please enter a valid Advance ID to delete!").show();
            return;
        }
        deleteAdvance(String.valueOf(advanceId));
    }

    public void deleteAdvance(String id) {
        int numericId = parseNumericId(id);
        if (numericId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Invalid Advance ID!").show();
            return;
        }

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "DELETE FROM worker_advances WHERE advance_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, numericId);

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Advance deleted successfully!").show();
                loadAdvancesTable();
                cleanAdvanceFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Advance not found!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }
}