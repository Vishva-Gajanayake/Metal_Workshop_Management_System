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
import lk.ijse.metalworkshopmanagementsystem.model.User;
import lk.ijse.metalworkshopmanagementsystem.model.Worker;
import lk.ijse.metalworkshopmanagementsystem.model.tbl.WorkerTbl;

public class WorkersController implements Initializable{
    
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

        loadRoleCombo();
        loadUserComboBox();
        
        loadWorkerTable();
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
        String userId = userComboBox.getSelectionModel().getSelectedItem();
        LocalDate joinedDate = joinDatePicker.getValue();

        if (!fullName.matches(NAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Worker Name").show();
        } else if (roleTtile == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a Role Title").show();
        } else if (!phone.matches(PHONE_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Phone Number").show();
        } else if (!dailyWage.matches(MONEY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid labour Rate").show();
        } else if (userId == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a User").show();
        } else if(joinedDate == null) {
            new Alert(Alert.AlertType.ERROR, "Please select the Joid Date").show();
        }else {

            Worker workerModel = new Worker(
                    userId.substring(1, 4),
                    fullName,
                    roleTtile,
                    phone,
                    Double.parseDouble(dailyWage),
                    joinedDate,
                    true
            );

            addWorker(workerModel);
        }
    }
    
    private void addWorker(Worker worker) {
        try {

            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT "
                    + "user_id "
                    + "FROM workers";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {

                String name = result.getString("user_id");

                if (name.equals(worker.getUserId())) {

                    String updateQuery = "UPDATE workers SET "
                            + "is_active=true "
                            + "WHERE user_id=?";

                    PreparedStatement pstmt = conn.prepareStatement(updateQuery);

                    pstmt.setString(1, worker.getUserId());

                    int result1 = pstmt.executeUpdate();

                    if (result1 > 0) {

                        new Alert(Alert.AlertType.INFORMATION,
                                "Worker saved successfully!").show();

                        loadWorkerTable();
                        cleanWorkerFields();
                        return;

                    } else {
                        new Alert(Alert.AlertType.ERROR,
                                "Something went wrong!").show();
                    }

                    pstmt.close();

                }

            }

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

            pstmt.setString(1, worker.getUserId());
            pstmt.setString(2, worker.getFullName());
            pstmt.setString(3, worker.getRoleTitle().name());
            pstmt.setString(4, worker.getPhone());
            pstmt.setDouble(5, worker.getDailyWage());
            pstmt.setObject(6, worker.getJoinDate());
            pstmt.setObject(7, worker.isIsActive());

            int result1 = pstmt.executeUpdate();

            if (result1 > 0) {
                new Alert(Alert.AlertType.INFORMATION,
                        "Worker saved successfully!").show();
                        loadWorkerTable();
                        cleanWorkerFields();
            } else {
                new Alert(Alert.AlertType.ERROR,
                        "Something went wrong!").show();
            }

            pstmt.close();

            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Something went wrong!").show();
        }
    }
    
    //---------------------- UPDATE WORKER TABLE ----------------------
    
    @FXML
    void clickBtnUpdate(ActionEvent event) {
        
        String workerId = searchTxtField.getText().trim();
        String fullName = fullNameTxtField.getText().trim();
        Role roleTtile = roleComboBox.getSelectionModel().getSelectedItem();
        String phone = phoneTxtField.getText().trim();
        String dailyWage = dailyWageTxtField.getText().trim();
        String userId = userComboBox.getSelectionModel().getSelectedItem();
        LocalDate joinedDate = joinDatePicker.getValue();

        if (!fullName.matches(NAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Worker Name").show();
        } else if (roleTtile == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a Role Title").show();
        } else if (!phone.matches(PHONE_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Phone Number").show();
        } else if (!dailyWage.matches(MONEY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid labour Rate").show();
        } else if (userId == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a User").show();
        } else if (joinedDate == null) {
            new Alert(Alert.AlertType.ERROR, "Please select the Joid Date").show();
        } else {

            Worker workerModel = new Worker(
                    workerId.substring(1, 4),
                    userId.substring(1, 4),
                    fullName,
                    roleTtile,
                    phone,
                    Double.parseDouble(dailyWage),
                    joinedDate,
                    true
            );

            updateWorker(workerModel);
        }
    }
    
    private void updateWorker(Worker workerModel) {

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

            pstmt.setString(1, workerModel.getUserId());
            pstmt.setString(2, workerModel.getFullName());
            pstmt.setString(3, workerModel.getRoleTitle().name());
            pstmt.setString(4, workerModel.getPhone());
            pstmt.setDouble(5, workerModel.getDailyWage());
            pstmt.setObject(6, workerModel.getJoinDate());
            pstmt.setString(7, workerModel.getWorkerId());

            int result = pstmt.executeUpdate();

            if (result > 0) {

                new Alert(Alert.AlertType.INFORMATION,
                        "Worker update successfully!").show();

                loadWorkerTable();
                cleanWorkerFields();

            } else {
                new Alert(Alert.AlertType.ERROR,
                        "Something went wrong!").show();
            }

            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Something went wrong!");
        }
    }
    
    //---------------------- DELETE WORKER ----------------------
    
    @FXML
    void clickBtnDelete(ActionEvent event) {

        deleteWorker(searchTxtField.getText().trim());
    }
    
    private void deleteWorker(String workerId) {

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "UPDATE workers SET "
                    + "is_active=false "
                    + "WHERE worker_id=?";

            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, workerId.substring(2));

            int result = pstmt.executeUpdate();

            if (result > 0) {

                new Alert(Alert.AlertType.INFORMATION,
                        "Worker delete successfully!").show();

                loadWorkerTable();
                cleanWorkerFields();

            } else {
                new Alert(Alert.AlertType.ERROR,
                        "Something went wrong!").show();
            }

            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something went wrong!");
        }
    }
    
    //---------------------- LOAD WORKER TABLE ----------------------
    
    private void loadWorkerTable() {

        List<Worker> workerList = getAllWorkers();
        List<WorkerTbl> workerTblList = new ArrayList<>();

        for (Worker worker : workerList) {

            WorkerTbl workerTbl = new WorkerTbl(
                    worker.getWorkerId(),
                    worker.getFullName(),
                    worker.getRoleTitle().toString(),
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

            String query = "SELECT "
                    + "* "
                    + "FROM workers "
                    + "WHERE is_active = '1' "
                    + "ORDER BY worker_id DESC ";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {

                String id = String.format("W%03d", result.getInt("worker_id"));
                String name = result.getString("full_name");
                String role = result.getString("role_title");
                String phone = result.getString("phone");
                String dailyWage = result.getString("daily_wage");

                Worker worker = new Worker();

                worker.setWorkerId(id);
                worker.setFullName(name);
                worker.setRoleTitle(toRole(role));
                worker.setPhone(phone);
                worker.setDailyWage(Double.parseDouble(dailyWage));

                workerList.add(worker);
            }

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
    }
    
    //---------------------- SEARCH SERVICE CATEGORY ----------------------

    @FXML
    void clickBtnSearch(ActionEvent event) {
        
        fillFields(searchTxtField.getText().trim());
    }

    @FXML
    void pressSearchWorker(KeyEvent event) {
        
        if (event.getCode() == KeyCode.ENTER) {

            fillFields(searchTxtField.getText().trim());
        }
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

        try {

            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM workers WHERE worker_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            String numId = id.substring(2);
            pstmt.setString(1, numId);

            ResultSet result = pstmt.executeQuery();

            if (result.next()) {

                String name = result.getString("full_name");
                String role = result.getString("role_title");
                String phone = result.getString("phone");
                double dailyWage = Double.parseDouble(result.getString("daily_wage"));
                String user = result.getString("user_id");
                String joinedDate = result.getString("join_date");

                workerModel = new Worker();

                workerModel.setFullName(name);
                workerModel.setRoleTitle(toRole(role));
                workerModel.setPhone(phone);
                workerModel.setDailyWage(dailyWage);
                workerModel.setUserId(user);
                workerModel.setJoinDate(LocalDate.parse(joinedDate));

            } else {
                new Alert(Alert.AlertType.ERROR, "Service Category not found!").show();
            }

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

        try {

            String query = "SELECT * FROM users WHERE user_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, userId);

            ResultSet result = pstmt.executeQuery();

            while (result.next()) {
                String id = String.format("U%03d", result.getInt("user_id"));
                String userName = result.getString("username");
                user = id + "  " + userName;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return user;
    }
    
}
