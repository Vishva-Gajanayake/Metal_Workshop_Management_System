package lk.ijse.metalworkshopmanagementsystem.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.metalworkshopmanagementsystem.model.Worker;
import lk.ijse.metalworkshopmanagementsystem.model.tbl.WageAdvancesTbl;

public class WagesAdvancesController implements Initializable{
    
    @FXML
    private DatePicker advanceDatePicker;

    @FXML
    private TextField amountTxtField;

    @FXML
    private TextField reasonTxtField;

    @FXML
    private Button saveBtn;

    @FXML
    private Button searchBtn;

    @FXML
    private TextField searchTxtField;

    @FXML
    private AnchorPane wageAdvancesPane;

    @FXML
    private TableView<?> wageAdvancesTbl;

    @FXML
    private ComboBox<?> workerCombo;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    void clickBtnSave(ActionEvent event) {

    }

    @FXML
    void clickBtnSearch(ActionEvent event) {

    }

    @FXML
    void pressSearchWageAdvances(KeyEvent event) {

    }
    
}
