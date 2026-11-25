package lk.ijse.metalworkshopmanagementsystem.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
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
import javafx.scene.control.PasswordField;
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
import lk.ijse.metalworkshopmanagementsystem.model.UserStatus;
import lk.ijse.metalworkshopmanagementsystem.model.tbl.UserTbl;

public class UserController implements Initializable {

    Connection conn;

    @FXML
    private Button clearBtn;

    @FXML
    private TableColumn<UserTbl, String> colFullName;

    @FXML
    private TableColumn<UserTbl, Role> colRole;

    @FXML
    private TableColumn<UserTbl, UserStatus> colStatus;

    @FXML
    private TableColumn<UserTbl, String> colUserId;

    @FXML
    private TableColumn<UserTbl, String> colUserName;

    @FXML
    private PasswordField confirmPassTxtField;

    @FXML
    private Button deleteBtn;

    @FXML
    private TextField fullNameTxtField;

    @FXML
    private PasswordField passwordTxtField;

    @FXML
    private ComboBox<Role> roleComboBox;

    @FXML
    private Button saveBtn;

    @FXML
    private Button searchBtn;

    @FXML
    private TextField searchTxtField;

    @FXML
    private Button updateBtn;

    @FXML
    private TableView<UserTbl> userTbl;

    @FXML
    private TextField usernameTxtField;

    @FXML
    private AnchorPane usersPane;

    //---------------------- REGEX ----------------------

    private static final String USERNAME_REGEX = "^[A-Za-z][A-Za-z0-9_]{3,19}$";
    private static final String PASSWORD_REGEX = "^.{8,}$";
    private static final String NAME_REGEX = "^[A-Za-z]+(?:\\s+[A-Za-z]+)*$";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("username"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        userTbl.setOnMouseClicked(event -> {
            UserTbl selected = userTbl.getSelectionModel().getSelectedItem();
            if (selected != null) {
                searchTxtField.setText(selected.getUserId());
                fillFields(selected.getUserId());
            }
        });

        loadUserTable();
        loadRoleCombo();
    }

    private int parseUserId(String rawId) {
        if (rawId == null) {
            return -1;
        }
        String digits = rawId.trim().replaceAll("\\D+", "");
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

    @FXML
    void clickBtnSave(ActionEvent event) {
        String username = usernameTxtField.getText().trim();
        String fullName = fullNameTxtField.getText().trim();
        Role roleTtile = roleComboBox.getSelectionModel().getSelectedItem();
        String password = passwordTxtField.getText().trim();
        String confirmedPassword = confirmPassTxtField.getText().trim();

        if (!fullName.matches(NAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Full Name").show();
        } else if (roleTtile == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a Role Title").show();
        } else if (!username.matches(USERNAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Username").show();
        } else if (!password.matches(PASSWORD_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Password").show();
        } else if (!password.equals(confirmedPassword)) {
            confirmPassTxtField.setText("");
            new Alert(Alert.AlertType.ERROR, "Passwords not matched").show();
        } else {
            User userModel = new User();
            userModel.setUsername(username);
            userModel.setFullName(fullName);
            userModel.setPassword(password);
            userModel.setRole(roleTtile);
            userModel.setIsActive(true);
            userModel.setCreatedAt(LocalDateTime.now());

            addUser(userModel);
        }
    }

    //---------------------- SAVE USER ----------------------

    private void addUser(User userModel) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT username FROM users";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String userName = result.getString("username");

                if (userName.equals(userModel.getUsername())) {
                    new Alert(Alert.AlertType.INFORMATION, "Username already existed!").show();
                    cleanUserFields();
                    stmt.close();
                    return;
                }
            }
            stmt.close();

            String queryInsert = "INSERT INTO users("
                    + "username, "
                    + "password_hash, "
                    + "full_name, "
                    + "role, "
                    + "is_active, "
                    + "created_at) "
                    + "VALUES(?,?,?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(queryInsert);

            pstmt.setString(1, userModel.getUsername());
            pstmt.setString(2, userModel.getPassword());
            pstmt.setString(3, userModel.getFullName());
            pstmt.setString(4, userModel.getRole().name());
            pstmt.setObject(5, userModel.isIsActive());
            pstmt.setObject(6, userModel.getCreatedAt());

            int result1 = pstmt.executeUpdate();
            pstmt.close();

            if (result1 > 0) {
                new Alert(Alert.AlertType.INFORMATION, "User saved successfully!").show();
                loadUserTable();
                cleanUserFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Something went wrong").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    //---------------------- UPDATE USER ----------------------

    @FXML
    void clickBtnUpdate(ActionEvent event) {
        int userId = parseUserId(searchTxtField.getText());
        if (userId <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please search or select a valid User ID first!").show();
            return;
        }

        String username = usernameTxtField.getText().trim();
        String fullName = fullNameTxtField.getText().trim();
        Role roleTtile = roleComboBox.getSelectionModel().getSelectedItem();
        String password = passwordTxtField.getText().trim();
        String confirmedPassword = confirmPassTxtField.getText().trim();

        if (!fullName.matches(NAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Full Name").show();
        } else if (roleTtile == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a Role Title").show();
        } else if (!username.matches(USERNAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Username").show();
        } else if (!password.matches(PASSWORD_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Password").show();
        } else if (!password.equals(confirmedPassword)) {
            confirmPassTxtField.setText("");
            new Alert(Alert.AlertType.ERROR, "Passwords not matched").show();
        } else {
            User userModel = new User();
            userModel.setUserId(String.valueOf(userId));
            userModel.setUsername(username);
            userModel.setFullName(fullName);
            userModel.setPassword(password);
            userModel.setRole(roleTtile);

            updateUser(userModel);
        }
    }

    private void updateUser(User userModel) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "UPDATE users SET "
                    + "username=?, "
                    + "password_hash=?, "
                    + "full_name=?, "
                    + "role=? "
                    + "WHERE user_id=?";

            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, userModel.getUsername());
            pstmt.setString(2, userModel.getPassword());
            pstmt.setString(3, userModel.getFullName());
            pstmt.setString(4, userModel.getRole().name());
            pstmt.setInt(5, Integer.parseInt(userModel.getUserId()));

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "User updated successfully!").show();
                loadUserTable();
                cleanUserFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "User not found or not updated!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    //---------------------- DELETE USER ----------------------

    @FXML
    void clickBtnDelete(ActionEvent event) {
        int userId = parseUserId(searchTxtField.getText());
        if (userId <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please search or select a valid User ID first!").show();
            return;
        }
        deleteWorker(userId);
    }

    private void deleteWorker(int userId) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "UPDATE users SET "
                    + "is_active=false "
                    + "WHERE user_id=?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "User deleted successfully!").show();
                loadUserTable();
                cleanUserFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "User not found!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    //---------------------- LOAD USERS TABLE ----------------------

    private void loadUserTable() {
        List<User> usersList = getAllUsers();
        List<UserTbl> userTblList = new ArrayList<>();

        for (User userModel : usersList) {
            UserStatus status = userModel.isIsActive() ? UserStatus.ACTIVE : UserStatus.CLOSED;

            UserTbl userTblModel = new UserTbl(
                    userModel.getUserId(),
                    userModel.getUsername(),
                    userModel.getFullName(),
                    userModel.getRole(),
                    status
            );

            userTblList.add(userTblModel);
        }

        ObservableList<UserTbl> obList
                = FXCollections.observableArrayList(userTblList);

        userTbl.setItems(obList);
    }

    private List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM users ORDER BY user_id DESC";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String id = String.format("U%03d", result.getInt("user_id"));
                String username = result.getString("username");
                String name = result.getString("full_name");
                String role = result.getString("role");
                boolean status = result.getBoolean("is_active");

                User userModel = new User();
                userModel.setUserId(id);
                userModel.setUsername(username);
                userModel.setFullName(name);
                userModel.setRole(toRole(role));
                userModel.setIsActive(status);

                userList.add(userModel);
            }

            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return userList;
    }

    private Role toRole(String text) {
        try {
            return Role.valueOf(text);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    //---------------------- CLEAN USER FORM ----------------------

    @FXML
    void clickClearBtn(ActionEvent event) {
        cleanUserFields();
    }

    private void cleanUserFields() {
        searchTxtField.setText("");
        fullNameTxtField.setText("");
        roleComboBox.setValue(null);
        passwordTxtField.setText("");
        confirmPassTxtField.setText("");
        usernameTxtField.setText("");
        userTbl.getSelectionModel().clearSelection();
    }

    //---------------------- SEARCH USER ----------------------

    @FXML
    void clickBtnSearch(ActionEvent event) {
        performSearch();
    }

    @FXML
    void pressSearchUser(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            performSearch();
        }
    }

    private void performSearch() {
        int userId = parseUserId(searchTxtField.getText());
        if (userId <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please enter a valid User ID (e.g., U001 or 1)").show();
            return;
        }
        fillFields(String.valueOf(userId));
    }

    private void fillFields(String id) {
        User userModel = searchUser(id);

        if (userModel != null) {
            fullNameTxtField.setText(userModel.getFullName());
            roleComboBox.setValue(userModel.getRole());
            usernameTxtField.setText(userModel.getUsername());
            passwordTxtField.setText(userModel.getPassword());
            confirmPassTxtField.setText(userModel.getPassword());
        }
    }

    private User searchUser(String id) {
        User userModel = null;
        int numericId = parseUserId(id);

        if (numericId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Invalid User ID!").show();
            return null;
        }

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM users WHERE user_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, numericId);

            ResultSet result = pstmt.executeQuery();

            if (result.next()) {
                String name = result.getString("full_name");
                String role = result.getString("role");
                String username = result.getString("username");
                String password = result.getString("password_hash");

                userModel = new User();
                userModel.setFullName(name);
                userModel.setRole(toRole(role));
                userModel.setUsername(username);
                userModel.setPassword(password);
            } else {
                new Alert(Alert.AlertType.ERROR, "User not found!").show();
            }

            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return userModel;
    }
}