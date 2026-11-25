package lk.ijse.metalworkshopmanagementsystem.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import lk.ijse.metalworkshopmanagementsystem.model.Supplier;
import lk.ijse.metalworkshopmanagementsystem.model.tbl.SuppliersTbl;

public class SuppliersController implements Initializable {

    Connection conn;

    @FXML
    private AnchorPane suppliersPane;

    @FXML
    private TextField addressTxtField;

    @FXML
    private TextField contactPersonTxtField;

    @FXML
    private TextField nameTxtField;

    @FXML
    private TextField phoneTxtField;

    @FXML
    private TextField searchTxtField;

    @FXML
    private Button saveBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private Button updateBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private TableColumn<SuppliersTbl, String> contactPersonCol;

    @FXML
    private TableColumn<SuppliersTbl, String> supAddressCol;

    @FXML
    private TableColumn<SuppliersTbl, String> supIdCol;

    @FXML
    private TableColumn<SuppliersTbl, String> supNameCol;

    @FXML
    private TableColumn<SuppliersTbl, String> supPhoneCol;

    @FXML
    private TableView<SuppliersTbl> supplierTbl;

    //---------------------- REGEX ----------------------

    private final String SUPPLIER_NAME_REGEX = "^[A-Za-z]+(?:\\s+[A-Za-z]+)*$";
    private final String SUPPLIER_PHONE_REGEX = "^(?:(?:\\+94|0094|94)[\\s.-]?(?:0)?[1-9]\\d{8}|0(?:[1-9]\\d{8}))$";
    private final String SUPPLIER_ADDRESS_REGEX = "^[A-Za-z0-9\\s,./#\\\\-]+$";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        supIdCol.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        supNameCol.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        contactPersonCol.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        supPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        supAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        supplierTbl.setOnMouseClicked(event -> {
            SuppliersTbl selected = supplierTbl.getSelectionModel().getSelectedItem();
            if (selected != null) {
                searchTxtField.setText(selected.getSupplierId());
                nameTxtField.setText(selected.getSupplierName());
                contactPersonTxtField.setText(selected.getContactPerson());
                phoneTxtField.setText(selected.getPhone());
                addressTxtField.setText(selected.getAddress());
            }
        });

        loadSupplierTable();
    }

    private int parseSupplierId(String rawId) {
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

    //---------------------- LOAD SUPPLIERS TABLE ----------------------

    private void loadSupplierTable() {
        List<Supplier> suppliersList = getAllClients();
        List<SuppliersTbl> suppliersTblList = new ArrayList<>();

        for (Supplier supplier : suppliersList) {
            SuppliersTbl supplierTbl = new SuppliersTbl(
                    supplier.getSupplierId(),
                    supplier.getSupplierName(),
                    supplier.getContactPerson(),
                    supplier.getPhone(),
                    supplier.getAddress()
            );

            suppliersTblList.add(supplierTbl);
        }

        ObservableList<SuppliersTbl> obList
                = FXCollections.observableArrayList(suppliersTblList);

        supplierTbl.setItems(obList);
    }

    public List<Supplier> getAllClients() {
        List<Supplier> suppliersList = new ArrayList<>();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT "
                    + "supplier_id, "
                    + "supplier_name, "
                    + "contact_person, "
                    + "phone, "
                    + "address "
                    + "FROM suppliers "
                    + "WHERE is_active = '1' "
                    + "ORDER BY supplier_id DESC";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String id = String.format("S%03d", result.getInt("supplier_id"));
                String name = result.getString("supplier_name");
                String contactPerson = result.getString("contact_person");
                String phone = result.getString("phone");
                String address = result.getString("address");

                Supplier supplier = new Supplier(id, name, contactPerson, phone, address);
                suppliersList.add(supplier);
            }

            result.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return suppliersList;
    }

    //---------------------- SAVE SUPPLIER ----------------------

    @FXML
    void clickBtnSave(ActionEvent event) {
        String supName = nameTxtField.getText().trim();
        String contactPerson = contactPersonTxtField.getText().trim();
        String supPhone = phoneTxtField.getText().trim();
        String supAddress = addressTxtField.getText().trim();

        if (!supName.matches(SUPPLIER_NAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Supplier Name").show();
        } else if (!contactPerson.matches(SUPPLIER_NAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Contact Person").show();
        } else if (!supPhone.matches(SUPPLIER_PHONE_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Supplier Phone").show();
        } else if (!supAddress.matches(SUPPLIER_ADDRESS_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Supplier Address").show();
        } else {
            Supplier suppliertModel = new Supplier(
                    supName,
                    contactPerson,
                    supPhone,
                    supAddress,
                    true
            );

            addSupplier(suppliertModel);
        }
    }

    private void addSupplier(Supplier supplierModel) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "INSERT INTO suppliers("
                    + "supplier_name, "
                    + "contact_person, "
                    + "phone, "
                    + "address, "
                    + "is_active) "
                    + "VALUES(?,?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, supplierModel.getSupplierName());
            pstmt.setString(2, supplierModel.getContactPerson());
            pstmt.setString(3, supplierModel.getPhone());
            pstmt.setString(4, supplierModel.getAddress());
            pstmt.setObject(5, supplierModel.isIsActive());

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Supplier saved successfully!").show();
                loadSupplierTable();
                cleanSupplierFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    //---------------------- UPDATE SUPPLIER ----------------------

    @FXML
    void clickBtnUpdate(ActionEvent event) {
        String supIdRaw = searchTxtField.getText().trim();
        int numericId = parseSupplierId(supIdRaw);

        if (numericId <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please search or select a valid Supplier ID first!").show();
            return;
        }

        String supName = nameTxtField.getText().trim();
        String contactPerson = contactPersonTxtField.getText().trim();
        String supPhone = phoneTxtField.getText().trim();
        String supAddress = addressTxtField.getText().trim();

        if (!supName.matches(SUPPLIER_NAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Supplier Name").show();
        } else if (!contactPerson.matches(SUPPLIER_NAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Contact Person").show();
        } else if (!supPhone.matches(SUPPLIER_PHONE_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Supplier Phone").show();
        } else if (!supAddress.matches(SUPPLIER_ADDRESS_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Supplier Address").show();
        } else {
            Supplier supModel = new Supplier(
                    String.format("S%03d", numericId),
                    supName,
                    contactPerson,
                    supPhone,
                    supAddress
            );

            updateSupplier(supModel, numericId);
        }
    }

    public void updateSupplier(Supplier sup) {
        int numericId = parseSupplierId(sup.getSupplierId());
        if (numericId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Invalid Supplier ID!").show();
            return;
        }
        updateSupplier(sup, numericId);
    }

    private void updateSupplier(Supplier sup, int numericId) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "UPDATE suppliers SET "
                    + "supplier_name=?, "
                    + "contact_person=?, "
                    + "phone=?, "
                    + "address=? "
                    + "WHERE supplier_id=?";

            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, sup.getSupplierName());
            pstmt.setString(2, sup.getContactPerson());
            pstmt.setString(3, sup.getPhone());
            pstmt.setString(4, sup.getAddress());
            pstmt.setInt(5, numericId);

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Supplier updated successfully!").show();
                loadSupplierTable();
                cleanSupplierFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Supplier not found or not updated!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    //---------------------- DELETE SUPPLIER ----------------------

    @FXML
    void clickBtnDelete(ActionEvent event) {
        int numericId = parseSupplierId(searchTxtField.getText());

        if (numericId <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please search or select a valid Supplier ID first!").show();
            return;
        }

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "UPDATE suppliers SET is_active=false WHERE supplier_id=?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, numericId);

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Supplier deleted successfully!").show();
                loadSupplierTable();
                cleanSupplierFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Supplier not found!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    //---------------------- CLEAN SUPPLIER FORM ----------------------

    @FXML
    void clickClearBtn(ActionEvent event) {
        cleanSupplierFields();
    }

    private void cleanSupplierFields() {
        searchTxtField.setText("");
        nameTxtField.setText("");
        contactPersonTxtField.setText("");
        phoneTxtField.setText("");
        addressTxtField.setText("");
        supplierTbl.getSelectionModel().clearSelection();
    }

    //---------------------- SEARCH SUPPLIER ----------------------

    @FXML
    void clickBtnSearch(ActionEvent event) {
        performSearch();
    }

    @FXML
    void pressSearchSupplier(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            performSearch();
        }
    }

    private void performSearch() {
        int numericId = parseSupplierId(searchTxtField.getText());
        if (numericId <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please enter a valid Supplier ID (e.g., S001 or 1)").show();
            return;
        }

        Supplier sup = searchSupplier(numericId);

        if (sup != null) {
            searchTxtField.setText(String.format("S%03d", numericId));
            nameTxtField.setText(sup.getSupplierName());
            contactPersonTxtField.setText(sup.getContactPerson());
            phoneTxtField.setText(sup.getPhone());
            addressTxtField.setText(sup.getAddress());
        }
    }

    private Supplier searchSupplier(int numericId) {
        Supplier sup = null;

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM suppliers WHERE supplier_id=? AND is_active = '1'";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, numericId);

            ResultSet result = pstmt.executeQuery();

            if (result.next()) {
                String name = result.getString("supplier_name");
                String contactPerson = result.getString("contact_person");
                String phone = result.getString("phone");
                String address = result.getString("address");

                sup = new Supplier();
                sup.setSupplierName(name);
                sup.setContactPerson(contactPerson);
                sup.setPhone(phone);
                sup.setAddress(address);
            } else {
                new Alert(Alert.AlertType.ERROR, "Supplier not found!").show();
            }

            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return sup;
    }
}