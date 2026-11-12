package lk.ijse.metalworkshopmanagementsystem.controller;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;

public class LoginController implements Initializable{
    
    @FXML
    private Button loginBtn;

    @FXML
    private Text loginStatusLabel;

    @FXML
    private PasswordField passwordTxt;

    @FXML
    private ToggleButton showPasswordToggle;

    @FXML
    private TextField usernameTxt;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
}
