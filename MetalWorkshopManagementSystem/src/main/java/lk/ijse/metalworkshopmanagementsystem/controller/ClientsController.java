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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.metalworkshopmanagementsystem.db.DBConnection;
import lk.ijse.metalworkshopmanagementsystem.model.Client;
import lk.ijse.metalworkshopmanagementsystem.model.tbl.ClientsTbl;


public class ClientsController implements Initializable {
    
    Connection conn;
    
    @FXML
    private AnchorPane ClientPane;

    @FXML
    private TextField addressTxtField;

    @FXML
    private Button clearBtn;

    @FXML
    private TableView<ClientsTbl> clientsTbl;

    @FXML
    private TableColumn<ClientsTbl, String> colAddress;

    @FXML
    private TableColumn<ClientsTbl, String> colEmail;

    @FXML
    private TableColumn<ClientsTbl, String> colId;

    @FXML
    private TableColumn<ClientsTbl, String> colName;

    @FXML
    private TableColumn<ClientsTbl, String> colPhone;

    @FXML
    private Button deleteBtn;

    @FXML
    private TextField emailTxtField;

    @FXML
    private TextField nameTxtField;

    @FXML
    private Button newBtn;

    @FXML
    private TextField phoneTxtField;

    @FXML
    private Button saveBtn;

    @FXML
    private TextField searchTxtField;

    @FXML
    private Button updateBtn;
    
    @FXML
    private Button searchBtn;
    
    //---------------------- REGEX ----------------------
    
    private final String CLIENT_NAME_REGEX = "^[A-Za-z]+(?:\\s+[A-Za-z]+)*$";
    private final String CLIENT_PHONE_REGEX = "^(?:(?:\\+94|0094|94)[\\s.-]?(?:0)?[1-9]\\d{8}|0(?:[1-9]\\d{8}))$";
    private final String CLIENT_EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private final String CLIENT_ADDRESS_REGEX = "^[A-Za-z0-9,\\/\\\\]+$";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        colId.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        
        loadClientTable();
    }
    
    //---------------------- SAVE CLIENT ----------------------
    
    @FXML
    void clickSaveBtn(ActionEvent event) {
        
        String clientName = nameTxtField.getText().trim();
        String clientPhone = phoneTxtField.getText().trim();
        String clientEmail = emailTxtField.getText().trim();
        String clientAddress = addressTxtField.getText().trim();
        
        if (!clientName.matches(CLIENT_NAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Client Name").show();
        } else if (!clientPhone.matches(CLIENT_PHONE_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Client Phone").show();
        } else if (!clientEmail.matches(CLIENT_EMAIL_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Client E-Mail").show();
        } else if (!clientAddress.matches(CLIENT_ADDRESS_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Client Address").show();
        } else {
            
            Client clientModel = new Client(
                    clientName, 
                    clientPhone, 
                    clientEmail, 
                    clientAddress, 
                    LocalDateTime.now(), 
                    true
            );
            
            addCustomer(clientModel);
        }
    }

    private void addCustomer(Client clientModel) {
        try {
            
            conn = DBConnection.getInstance().getConnection();
            
            String queryInsert = "INSERT INTO clients("
                    + "full_name, "
                    + "phone, "
                    + "email, "
                    + "address, "
                    + "created_at, "
                    + "is_active) "
                    + "VALUES(?,?,?,?,?,?)";
            
            PreparedStatement pstmt = conn.prepareStatement(queryInsert);

            pstmt.setString(1, clientModel.getFullName());
            pstmt.setString(2, clientModel.getPhone());
            pstmt.setString(3, clientModel.getEmail());
            pstmt.setString(4, clientModel.getAddress());
            pstmt.setObject(5, clientModel.getCreatedAt());
            pstmt.setObject(6, clientModel.isIsActive());

            int result1 = pstmt.executeUpdate();

            if (result1 > 0) {
                new Alert(Alert.AlertType.INFORMATION, 
                        "Client saved successfully!").show();
                loadClientTable();
                cleanClientFields();
            } else {
                new Alert(Alert.AlertType.ERROR, 
                        "Something went wrong!").show();
            }
            
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, 
                    "Something went wrong!").show();
        }
    }
    
    //---------------------- UPDATE CLIENT ----------------------
    
    @FXML
    void clickUpdateBtn(ActionEvent event) {
        
        String clientId = searchTxtField.getText().trim();
        String clientName = nameTxtField.getText().trim();
        String clientPhone = phoneTxtField.getText().trim();
        String clientEmail = emailTxtField.getText().trim();
        String clientAddress = addressTxtField.getText().trim();

        if (!clientName.matches(CLIENT_NAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Client Name").show();
        } else if (!clientPhone.matches(CLIENT_PHONE_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Client Phone").show();
        } else if (!clientEmail.matches(CLIENT_EMAIL_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Client E-Mail").show();
        } else if (!clientAddress.matches(CLIENT_ADDRESS_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Client Address").show();
        } else {
            
            Client clientModel = new Client(
                    clientId, 
                    clientName, 
                    clientPhone, 
                    clientEmail, 
                    clientAddress
            );
            
            updateClient(clientModel);

        }
    }
    
    private void updateClient(Client client) {
        
        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "UPDATE clients SET "
                    + "full_name=?, "
                    + "phone=?, "
                    + "email=?, "
                    + "address=? "
                    + "WHERE client_id=?";
            
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, client.getFullName());
            pstmt.setString(2, client.getPhone());
            pstmt.setString(3, client.getEmail());
            pstmt.setString(4, client.getAddress());
            pstmt.setString(5, client.getClientId().substring(1));

            int result = pstmt.executeUpdate();

            if (result > 0) {
                
                new Alert(Alert.AlertType.INFORMATION, 
                        "Client update successfully!").show();
                
                loadClientTable();
                cleanClientFields();
                
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
    
    //---------------------- DELETE CLIENTS ----------------------
    
    @FXML
    void clickBtnDelete(ActionEvent event) {

        String clientId = searchTxtField.getText().trim();
        
        try {
            conn = DBConnection.getInstance().getConnection();
            
            String query = "UPDATE clients SET "
                    + "is_active=false "
                    + "WHERE client_id=?";

            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, clientId.substring(1));
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {

                new Alert(Alert.AlertType.INFORMATION,
                        "Client delete successfully!").show();

                loadClientTable();
                cleanClientFields();

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
    
    //---------------------- LOAD CLIENTS TABLE ----------------------
    
    private void loadClientTable() {

        List<Client> clientList = getAllClients();
        List<ClientsTbl> clientTblList = new ArrayList<>();

        for (Client client : clientList) {
            
            ClientsTbl clientTbl = new ClientsTbl(
                    client.getClientId(), 
                    client.getFullName(), 
                    client.getPhone(), 
                    client.getEmail(), 
                    client.getAddress()
            );
            
            clientTblList.add(clientTbl);
        }

        ObservableList<ClientsTbl> obList = 
                FXCollections.observableArrayList(clientTblList);
        
        clientsTbl.setItems(obList);
    }
    
    private List<Client> getAllClients() {

        List<Client> clientsList = new ArrayList<>();

        try {
            conn = DBConnection.getInstance().getConnection();
                
            String query = "SELECT "
                    + "client_id, "
                    + "full_name, "
                    + "phone, "
                    + "email, "
                    + "address "
                    + "FROM clients "
                    + "WHERE is_active = '1' "
                    + "ORDER BY client_id DESC ";
            
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                
                String id = String.format("C%03d", result.getInt("client_id"));
                String name = result.getString("full_name");
                String phone = result.getString("phone");
                String email = result.getString("email");
                String address = result.getString("address");

                Client client = new Client(id, name, phone, email, address);
                clientsList.add(client);
            }

            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return clientsList;

    }
    
    //---------------------- CLEAN CLIENT FORM ----------------------
    
    @FXML
    void clickClearBtn(ActionEvent event) {
        
        cleanClientFields();

    }
    
    private void cleanClientFields() {
        
        searchTxtField.setText("");
        nameTxtField.setText("");
        addressTxtField.setText("");
        phoneTxtField.setText("");
        emailTxtField.setText("");
    }
    
    //---------------------- SEARCH CLIENT ----------------------
    
    @FXML
    void clickBtnSearch(ActionEvent event) {
        
        String id = searchTxtField.getText();
        Client client = searchCustomer(id);

        if (client != null) {
            nameTxtField.setText(client.getFullName());
            phoneTxtField.setText(client.getPhone());
            emailTxtField.setText(client.getEmail());
            addressTxtField.setText(client.getAddress());
        }
    }
    
    @FXML
    void pressSearchClient(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

            String id = searchTxtField.getText();
            Client client = searchCustomer(id);

            if (client != null) {
                nameTxtField.setText(client.getFullName());
                phoneTxtField.setText(client.getPhone());
                emailTxtField.setText(client.getEmail());
                addressTxtField.setText(client.getAddress());
            }
        }
    }
    
    private Client searchCustomer(String id) {

        Client client = null;

        try {

            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM clients WHERE client_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            String numId = id.substring(1);
            pstmt.setString(1, numId);

            ResultSet result = pstmt.executeQuery();

            if (result.next()) {

                String name = result.getString("full_name");
                String phone = result.getString("phone");
                String email = result.getString("email");
                String address = result.getString("address");

                client = new Client();
                
                client.setFullName(name);
                client.setPhone(phone);
                client.setEmail(email);
                client.setAddress(address);

            } else {
                new Alert(Alert.AlertType.ERROR, "Client not found!").show();
            }

            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return client;
    }
    
}
