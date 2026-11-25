package lk.ijse.metalworkshopmanagementsystem.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import lk.ijse.metalworkshopmanagementsystem.MetalWorkshopManagementSystem;
import lk.ijse.metalworkshopmanagementsystem.db.DBConnection;
import lk.ijse.metalworkshopmanagementsystem.model.Role;
import lk.ijse.metalworkshopmanagementsystem.model.User;

public class LoginController implements Initializable {

    public static User loggedUser = null;

    Connection conn;

    @FXML
    private Button loginBtn;

    @FXML
    private Text loginStatusLabel;

    @FXML
    private PasswordField passwordTxt;

    @FXML
    private TextField showPassTxtField;

    @FXML
    private Button showPasswordBtn;

    @FXML
    private TextField usernameTxt;

    private static final String adminUserName = "IJSE";
    private static final String adminPassword = "123";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showPasswordBtn.setText("SHOW");
        passwordTxt.setVisible(true);
        showPassTxtField.setVisible(false);
        loginStatusLabel.setText("");
    }

    @FXML
    void clickLogingBtn(ActionEvent event) throws IOException {
        String inputUserName = usernameTxt.getText().trim();
        String inputUserPassword = showPassTxtField.isVisible()
                ? showPassTxtField.getText().trim()
                : passwordTxt.getText().trim();

        login(inputUserName, inputUserPassword);
    }

    private void login(String inputUserName, String inputUserPassword) throws IOException {
        if (inputUserName.isEmpty() || inputUserPassword.isEmpty()) {
            loginStatusLabel.setText("Please enter username and password");
            return;
        }

        if (inputUserName.equals(adminUserName) && inputUserPassword.equals(adminPassword)) {
            loggedUser = new User("U000", adminUserName, adminPassword, "System Admin", Role.OWNER);
            loginStatusLabel.setText("");
            MetalWorkshopManagementSystem.getScene().setRoot(MetalWorkshopManagementSystem.loadFxml("Layout"));
        } else {
            selectUser(inputUserName, inputUserPassword);
        }
    }

    private void selectUser(String inputUserName, String inputUserPassword) throws IOException {
        List<User> users = getAllUsers();
        User matchedUser = null;

        for (User user : users) {
            if (user.getUsername().equals(inputUserName) && user.getPassword().equals(inputUserPassword)) {
                matchedUser = user;
                break;
            }
        }

        if (matchedUser != null) {
            loggedUser = matchedUser;
            loginStatusLabel.setText("");
            MetalWorkshopManagementSystem.getScene().setRoot(MetalWorkshopManagementSystem.loadFxml("Layout"));
        } else {
            loginStatusLabel.setText("Invalid Username or Password");
        }
    }

    private List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM users WHERE is_active = '1'";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String userId = String.format("U%03d", result.getInt("user_id"));
                String userName = result.getString("username");
                String userPassword = result.getString("password_hash");
                String fullName = result.getString("full_name");
                String role = result.getString("role");

                User user = new User(
                        userId,
                        userName,
                        userPassword,
                        fullName,
                        toRole(role)
                );

                userList.add(user);
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

    //---------------------- SHOW PASSWORD METHOD ----------------------

    @FXML
    void clickShowBtn(ActionEvent event) {
        if (showPassTxtField.isVisible()) {
            passwordTxt.setText(showPassTxtField.getText());
            showPassTxtField.setVisible(false);
            passwordTxt.setVisible(true);
            showPasswordBtn.setText("SHOW");
        } else {
            showPassTxtField.setText(passwordTxt.getText());
            showPassTxtField.setVisible(true);
            passwordTxt.setVisible(false);
            showPasswordBtn.setText("HIDE");
        }
    }
}