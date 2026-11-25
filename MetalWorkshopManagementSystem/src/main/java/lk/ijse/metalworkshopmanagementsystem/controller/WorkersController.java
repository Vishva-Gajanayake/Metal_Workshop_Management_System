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
import lk.ijse.metalworkshopmanagementsystem.model.Role;
import lk.ijse.metalworkshopmanagementsystem.model.Worker;
import lk.ijse.metalworkshopmanagementsystem.model.tbl.WorkerTbl;

public class WorkersController implements Initializable {

    Connection conn;

    @FXML
    private AnchorPane workersPane;

    @FXML
    private TextField dailyWageTxtField;

    @FXML
    private TextField fullNameTxtField;

    @FXML
    private TextField phoneTxtField;

    @FXML
    private TextField searchTxtField;

    @FXML
    private ComboBox<Role> roleComboBox;

    @FXML
    private ComboBox<String> userComboBox;

    @FXML
    private Button saveBtn;

    @FXML
    private Button searchBtn;

    @FXML
    private Button updateBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private DatePicker joinDatePicker;

    @FXML
    private TableColumn<WorkerTbl, Double> colDailyWage;

    @FXML
    private TableColumn<WorkerTbl, String> colName;

    @FXML
    private TableColumn<WorkerTbl, String> colPhone;

    @FXML
    private TableColumn<WorkerTbl, String> colRole;

    @FXML
    private TableColumn<WorkerTbl, String> colWorkerId;

    @FXML
    private TableView<WorkerTbl> workerTbl;

    //---------------------- REGEX ----------------------

    private static final String NAME_REGEX = "^[A-Za-z]+(?:\\s+[A-Za-z]+)*$";
    private static final String PHONE_REGEX = "^0\\d{9}$";
    private static final String MONEY_REGEX = "^\\d{1,8}(\\.\\d{1,2})?$";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colWorkerId.setCellValueFactory(new PropertyValueFactory<>("workerId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colDailyWage.setCellValueFactory(new PropertyValueFactory<>("dailyWage"));

        workerTbl.setOnMouseClicked(event -> {
            WorkerTbl selected = workerTbl.getSelectionModel().getSelectedItem();
            if (selected != null) {
                searchTxtField.setText(selected.getWorkerId());
                fillFields(selected.getWorkerId());
            }
        });

        loadRoleCombo();
        loadUserComboBox();
        loadWorkerTable();
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

    //---------------------- LOAD COMBO BOXES ----------------------

    private void loadRoleCombo() {
        roleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
    }

    private void loadUserComboBox() {
        List<String> userList = new ArrayList<>();
        ObservableList<String> userObList = FXCollections.observableArrayList();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM users ORDER BY user_id ASC";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String id = String.format("U%03d", result.getInt("user_id"));
                String userName = result.getString("username");
                userList.add(id + "  " + userName);
            }

            userObList.addAll(userList);
            userComboBox.setItems(userObList);

            result.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    //---------------------- SAVE WORKER ----------------------

    @FXML
    void clickBtnSave(ActionEvent event) {
        String fullName = fullNameTxtField.getText().trim();
        Role roleTtile = roleComboBox.getSelectionModel().getSelectedItem();
        String phone = phoneTxtField.getText().trim();
        String dailyWage = dailyWageTxtField.getText().trim();
        String selectedUser = userComboBox.getSelectionModel().getSelectedItem();
        LocalDate joinedDate = joinDatePicker.getValue();

        int numericUserId = parseNumericId(selectedUser);

        if (!fullName.matches(NAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Worker Name").show();
        } else if (roleTtile == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a Role Title").show();
        } else if (!phone.matches(PHONE_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Phone Number").show();
        } else if (!dailyWage.matches(MONEY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid labour Rate").show();
        } else if (selectedUser == null || numericUserId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please select a User").show();
        } else if (joinedDate == null) {
            new Alert(Alert.AlertType.ERROR, "Please select the Join Date").show();
        } else {
            Worker workerModel = new Worker(
                    String.valueOf(numericUserId),
                    fullName,
                    roleTtile,
                    phone,
                    Double.parseDouble(dailyWage),
                    joinedDate,
                    true
            );

            addWorker(workerModel, numericUserId);
        }
    }

    private void addWorker(Worker worker, int numericUserId) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT user_id FROM workers WHERE user_id=?";
            PreparedStatement checkStmt = conn.prepareStatement(query);
            checkStmt.setInt(1, numericUserId);
            ResultSet result = checkStmt.executeQuery();

            if (result.next()) {
                result.close();
                checkStmt.close();

                String updateQuery = "UPDATE workers SET is_active=true WHERE user_id=?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, numericUserId);
                int result1 = updateStmt.executeUpdate();
                updateStmt.close();

                if (result1 > 0) {
                    new Alert(Alert.AlertType.INFORMATION, "Worker already existed and was reactivated!").show();
                    loadWorkerTable();
                    cleanWorkerFields();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
                }
                return;
            }
            result.close();
            checkStmt.close();

            String queryInsert = "INSERT INTO workers("
                    + "user_id, "
                    + "full_name, "
                    + "role_title, "
                    + "phone, "
                    + "daily_wage, "
                    + "join_date, "
                    + "is_active) "
                    + "VALUES(?,?,?,?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(queryInsert);

            pstmt.setInt(1, numericUserId);
            pstmt.setString(2, worker.getFullName());
            pstmt.setString(3, worker.getRoleTitle().name());
            pstmt.setString(4, worker.getPhone());
            pstmt.setDouble(5, worker.getDailyWage());
            pstmt.setObject(6, worker.getJoinDate());
            pstmt.setObject(7, worker.isIsActive());

            int result1 = pstmt.executeUpdate();
            pstmt.close();

            if (result1 > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Worker saved successfully!").show();
                loadWorkerTable();
                cleanWorkerFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Something went wrong").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong here!").show();
        }
    }

    //---------------------- UPDATE WORKER TABLE ----------------------

    @FXML
    void clickBtnUpdate(ActionEvent event) {
        int workerId = parseNumericId(searchTxtField.getText());
        if (workerId <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please search or select a valid Worker ID first!").show();
            return;
        }

        String fullName = fullNameTxtField.getText().trim();
        Role roleTtile = roleComboBox.getSelectionModel().getSelectedItem();
        String phone = phoneTxtField.getText().trim();
        String dailyWage = dailyWageTxtField.getText().trim();
        String selectedUser = userComboBox.getSelectionModel().getSelectedItem();
        LocalDate joinedDate = joinDatePicker.getValue();

        int numericUserId = parseNumericId(selectedUser);

        if (!fullName.matches(NAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Worker Name").show();
        } else if (roleTtile == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a Role Title").show();
        } else if (!phone.matches(PHONE_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Phone Number").show();
        } else if (!dailyWage.matches(MONEY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid labour Rate").show();
        } else if (selectedUser == null || numericUserId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please select a User").show();
        } else if (joinedDate == null) {
            new Alert(Alert.AlertType.ERROR, "Please select the Join Date").show();
        } else {
            Worker workerModel = new Worker(
                    String.valueOf(workerId),
                    String.valueOf(numericUserId),
                    fullName,
                    roleTtile,
                    phone,
                    Double.parseDouble(dailyWage),
                    joinedDate,
                    true
            );

            updateWorker(workerModel, workerId, numericUserId);
        }
    }

    private void updateWorker(Worker workerModel, int workerId, int numericUserId) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "UPDATE workers SET "
                    + "user_id=?, "
                    + "full_name=?, "
                    + "role_title=?, "
                    + "phone=?, "
                    + "daily_wage=?, "
                    + "join_date=? "
                    + "WHERE worker_id=?";

            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, numericUserId);
            pstmt.setString(2, workerModel.getFullName());
            pstmt.setString(3, workerModel.getRoleTitle().name());
            pstmt.setString(4, workerModel.getPhone());
            pstmt.setDouble(5, workerModel.getDailyWage());
            pstmt.setObject(6, workerModel.getJoinDate());
            pstmt.setInt(7, workerId);

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Worker updated successfully!").show();
                loadWorkerTable();
                cleanWorkerFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Worker not found or not updated!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    //---------------------- DELETE WORKER ----------------------

    @FXML
    void clickBtnDelete(ActionEvent event) {
        int workerId = parseNumericId(searchTxtField.getText());
        if (workerId <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please search or select a valid Worker ID first!").show();
            return;
        }
        deleteWorker(workerId);
    }

    private void deleteWorker(int workerId) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "UPDATE workers SET is_active=false WHERE worker_id=?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, workerId);

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Worker deleted successfully!").show();
                loadWorkerTable();
                cleanWorkerFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Worker not found!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    //---------------------- LOAD WORKER TABLE ----------------------

    private void loadWorkerTable() {
        List<Worker> workerList = getAllWorkers();
        List<WorkerTbl> workerTblList = new ArrayList<>();

        for (Worker worker : workerList) {
            String roleText = (worker.getRoleTitle() != null) ? worker.getRoleTitle().name() : "";
            WorkerTbl workerTbl = new WorkerTbl(
                    worker.getWorkerId(),
                    worker.getFullName(),
                    roleText,
                    worker.getPhone(),
                    worker.getDailyWage()
            );

            workerTblList.add(workerTbl);
        }

        ObservableList<WorkerTbl> obList
                = FXCollections.observableArrayList(workerTblList);

        workerTbl.setItems(obList);
    }

    private List<Worker> getAllWorkers() {
        List<Worker> workerList = new ArrayList<>();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * "
                    + "FROM workers "
                    + "WHERE is_active = '1' "
                    + "ORDER BY worker_id DESC";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String id = String.format("W%03d", result.getInt("worker_id"));
                String name = result.getString("full_name");
                String role = result.getString("role_title");
                String phone = result.getString("phone");
                double dailyWage = result.getDouble("daily_wage");

                Worker worker = new Worker();
                worker.setWorkerId(id);
                worker.setFullName(name);
                worker.setRoleTitle(toRole(role));
                worker.setPhone(phone);
                worker.setDailyWage(dailyWage);

                workerList.add(worker);
            }

            result.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return workerList;
    }

    private Role toRole(String text) {
        try {
            return Role.valueOf(text);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    //---------------------- CLEAN WORKER FORM ----------------------

    @FXML
    void clickClearBtn(ActionEvent event) {
        cleanWorkerFields();
    }

    private void cleanWorkerFields() {
        searchTxtField.setText("");
        fullNameTxtField.setText("");
        roleComboBox.setValue(null);
        phoneTxtField.setText("");
        dailyWageTxtField.setText("");
        userComboBox.setValue(null);
        joinDatePicker.setValue(null);
        workerTbl.getSelectionModel().clearSelection();
    }

    //---------------------- SEARCH WORKER ----------------------

    @FXML
    void clickBtnSearch(ActionEvent event) {
        performSearch();
    }

    @FXML
    void pressSearchWorker(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            performSearch();
        }
    }

    private void performSearch() {
        int workerId = parseNumericId(searchTxtField.getText());
        if (workerId <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please enter a valid Worker ID (e.g., W001 or 1)").show();
            return;
        }
        fillFields(String.valueOf(workerId));
    }

    private void fillFields(String id) {
        Worker workerModel = searchWorker(id);

        if (workerModel != null) {
            fullNameTxtField.setText(workerModel.getFullName());
            roleComboBox.setValue(workerModel.getRoleTitle());
            phoneTxtField.setText(workerModel.getPhone());
            dailyWageTxtField.setText(String.valueOf(workerModel.getDailyWage()));
            userComboBox.setValue(getUserById(workerModel.getUserId()));
            joinDatePicker.setValue(workerModel.getJoinDate());
        }
    }

    private Worker searchWorker(String id) {
        Worker workerModel = null;
        int numericId = parseNumericId(id);

        if (numericId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Invalid Worker ID!").show();
            return null;
        }

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM workers WHERE worker_id=? AND is_active = '1'";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, numericId);

            ResultSet result = pstmt.executeQuery();

            if (result.next()) {
                String name = result.getString("full_name");
                String role = result.getString("role_title");
                String phone = result.getString("phone");
                double dailyWage = result.getDouble("daily_wage");
                String user = String.valueOf(result.getInt("user_id"));
                String joinedDate = result.getString("join_date");

                workerModel = new Worker();
                workerModel.setFullName(name);
                workerModel.setRoleTitle(toRole(role));
                workerModel.setPhone(phone);
                workerModel.setDailyWage(dailyWage);
                workerModel.setUserId(user);
                if (joinedDate != null && !joinedDate.isEmpty()) {
                    workerModel.setJoinDate(LocalDate.parse(joinedDate));
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Worker not found!").show();
            }

            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return workerModel;
    }

    //---------------------- GET USER NAME ----------------------

    private String getUserById(String userId) {
        String user = null;
        int numericUserId = parseNumericId(userId);
        if (numericUserId <= 0) {
            return null;
        }

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM users WHERE user_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, numericUserId);

            ResultSet result = pstmt.executeQuery();

            if (result.next()) {
                String id = String.format("U%03d", result.getInt("user_id"));
                String userName = result.getString("username");
                user = id + "  " + userName;
            }

            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return user;
    }
}