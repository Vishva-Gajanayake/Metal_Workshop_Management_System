package lk.ijse.metalworkshopmanagementsystem.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.metalworkshopmanagementsystem.db.DBConnection;
import lk.ijse.metalworkshopmanagementsystem.model.Inquiry;
import lk.ijse.metalworkshopmanagementsystem.model.InquiryStatus;
import lk.ijse.metalworkshopmanagementsystem.model.tbl.InquiryTbl;

public class InquiriesControllers implements Initializable {

    Connection conn;

    @FXML
    private AnchorPane InquiriesPane;

    @FXML
    private ComboBox<String> categoryCombo;

    @FXML
    private ComboBox<String> clientCombo;

    @FXML
    private ComboBox<InquiryStatus> statusCombo;

    @FXML
    private TableColumn<InquiryTbl, String> colCategory;

    @FXML
    private TableColumn<InquiryTbl, String> colClient;

    @FXML
    private TableColumn<InquiryTbl, String> colId;

    @FXML
    private TableColumn<InquiryTbl, String> colStatus;

    @FXML
    private TableColumn<InquiryTbl, LocalDate> colVistDate;

    @FXML
    private TableView<InquiryTbl> inquiryTbl;

    @FXML
    private Button saveBtn;

    @FXML
    private Button searchBtn;

    @FXML
    private Button updateBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private TextField descriptionTxtField;

    @FXML
    private TextField locationTxtField;

    @FXML
    private TextField searchTxtField;

    @FXML
    private DatePicker visitDatePicker;

    //---------------------- REGEX ----------------------

    private static final String ADDRESS_REGEX = "^[A-Za-z0-9/,.#\\-\\s]{3,100}$";
    private static final String TEXT_REGEX = "^[A-Za-z0-9 ,.'()/\\-]{3,255}$";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("inquiryId"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colVistDate.setCellValueFactory(new PropertyValueFactory<>("siteVisitDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("inquiryStatus"));

        inquiryTbl.setOnMouseClicked(e -> {
            InquiryTbl row = inquiryTbl.getSelectionModel().getSelectedItem();
            if (row != null) {
                searchTxtField.setText(row.getInquiryId());
                fillFields(row.getInquiryId());
            }
        });

        loadClientComboBox();
        loadCategoryComboBox();
        loadStatusCombo();

        loadInquiryTable();
    }

    private int parseNumericId(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
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

    private void loadClientComboBox() {
        List<String> clientList = new ArrayList<>();
        ObservableList<String> clientObList = FXCollections.observableArrayList();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM clients ORDER BY client_id ASC";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String id = String.format("C%03d", result.getInt("client_id"));
                String name = result.getString("full_name");
                clientList.add(id + "  " + name);
            }

            clientObList.addAll(clientList);
            clientCombo.setItems(clientObList);

            result.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    private void loadCategoryComboBox() {
        List<String> categoryList = new ArrayList<>();
        ObservableList<String> categoryObList = FXCollections.observableArrayList();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM service_categories ORDER BY category_id ASC";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String id = String.format("SC%03d", result.getInt("category_id"));
                String name = result.getString("category_name");
                categoryList.add(id + "  " + name);
            }

            categoryObList.addAll(categoryList);
            categoryCombo.setItems(categoryObList);

            result.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    private void loadStatusCombo() {
        statusCombo.setItems(FXCollections.observableArrayList(InquiryStatus.values()));
    }

    //---------------------- SAVE INQUIRY ----------------------

    @FXML
    void clickBtnSave(ActionEvent event) {
        String clientSel = clientCombo.getSelectionModel().getSelectedItem();
        String categorySel = categoryCombo.getSelectionModel().getSelectedItem();
        LocalDateTime inquiryDate = LocalDateTime.now();
        String siteLocation = locationTxtField.getText().trim();
        String description = descriptionTxtField.getText().trim();
        LocalDate visitDate = visitDatePicker.getValue();
        InquiryStatus status = statusCombo.getSelectionModel().getSelectedItem();

        int clientId = parseNumericId(clientSel);
        int categoryId = parseNumericId(categorySel);

        if (clientSel == null || clientId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please select a Client").show();
        } else if (categorySel == null || categoryId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please select a Service Category").show();
        } else if (!siteLocation.matches(ADDRESS_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Site Address").show();
        } else if (!description.matches(TEXT_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Description").show();
        } else if (visitDate == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a Site Visit Date").show();
        } else if (status == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a Status").show();
        } else {
            Inquiry inquiryModel = new Inquiry(
                    String.valueOf(clientId),
                    String.valueOf(categoryId),
                    inquiryDate,
                    siteLocation,
                    description,
                    visitDate,
                    status
            );
            addInquiry(inquiryModel);
        }
    }

    private void addInquiry(Inquiry inquiryModel) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String queryInsert = "INSERT INTO inquiries("
                    + "client_id, "
                    + "category_id, "
                    + "inquiry_date, "
                    + "site_location, "
                    + "description, "
                    + "site_vist_date, "
                    + "status) "
                    + "VALUES(?,?,?,?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(queryInsert);

            pstmt.setInt(1, Integer.parseInt(inquiryModel.getClientId()));
            pstmt.setInt(2, Integer.parseInt(inquiryModel.getCategoryId()));
            pstmt.setObject(3, inquiryModel.getInquiryDate());
            pstmt.setString(4, inquiryModel.getSiteLocation());
            pstmt.setString(5, inquiryModel.getDescription());
            pstmt.setObject(6, inquiryModel.getSiteVisitDate());
            pstmt.setString(7, inquiryModel.getInquiryStatus().name());

            int result1 = pstmt.executeUpdate();
            pstmt.close();

            if (result1 > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Inquiry saved successfully!").show();
                loadInquiryTable();
                cleanInquiryFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    //---------------------- UPDATE INQUIRY ----------------------

    @FXML
    void clickBtnUpdate(ActionEvent event) {
        int inquiryId = parseNumericId(searchTxtField.getText());
        String clientSel = clientCombo.getSelectionModel().getSelectedItem();
        String categorySel = categoryCombo.getSelectionModel().getSelectedItem();
        String siteLocation = locationTxtField.getText().trim();
        String description = descriptionTxtField.getText().trim();
        LocalDate visitDate = visitDatePicker.getValue();
        InquiryStatus status = statusCombo.getSelectionModel().getSelectedItem();

        int clientId = parseNumericId(clientSel);
        int categoryId = parseNumericId(categorySel);

        if (inquiryId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please search or select an Inquiry first").show();
        } else if (clientSel == null || clientId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please select a Client").show();
        } else if (categorySel == null || categoryId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please select a Service Category").show();
        } else if (!siteLocation.matches(ADDRESS_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Site Address").show();
        } else if (!description.matches(TEXT_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Description").show();
        } else if (visitDate == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a Site Visit Date").show();
        } else if (status == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a Status").show();
        } else {
            Inquiry inquiryModel = new Inquiry();
            inquiryModel.setInquiryId(String.valueOf(inquiryId));
            inquiryModel.setClientId(String.valueOf(clientId));
            inquiryModel.setCategoryId(String.valueOf(categoryId));
            inquiryModel.setSiteLocation(siteLocation);
            inquiryModel.setDescription(description);
            inquiryModel.setSiteVisitDate(visitDate);
            inquiryModel.setInquiryStatus(status);

            updateInquiry(inquiryModel);
        }
    }

    private void updateInquiry(Inquiry inquiryModel) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "UPDATE inquiries SET "
                    + "client_id=?, "
                    + "category_id=?, "
                    + "site_location=?, "
                    + "description=?, "
                    + "site_vist_date=?, "
                    + "status=? "
                    + "WHERE inquiry_id=?";

            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, Integer.parseInt(inquiryModel.getClientId()));
            pstmt.setInt(2, Integer.parseInt(inquiryModel.getCategoryId()));
            pstmt.setString(3, inquiryModel.getSiteLocation());
            pstmt.setString(4, inquiryModel.getDescription());
            pstmt.setObject(5, inquiryModel.getSiteVisitDate());
            pstmt.setString(6, inquiryModel.getInquiryStatus().name());
            pstmt.setInt(7, Integer.parseInt(inquiryModel.getInquiryId()));

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Inquiry updated successfully!").show();
                loadInquiryTable();
                cleanInquiryFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    //---------------------- DELETE INQUIRY ----------------------

    @FXML
    void clickBtnDelete(ActionEvent event) {
        int inquiryId = parseNumericId(searchTxtField.getText());
        if (inquiryId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please search or select an Inquiry to delete").show();
            return;
        }
        deleteInquiry(String.valueOf(inquiryId));
    }

    public void deleteInquiry(String inquiryId) {
        int id = parseNumericId(inquiryId);
        if (id <= 0) {
            new Alert(Alert.AlertType.ERROR, "Invalid Inquiry ID").show();
            return;
        }

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "DELETE FROM inquiries WHERE inquiry_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Inquiry deleted successfully!").show();
                loadInquiryTable();
                cleanInquiryFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Inquiry not found!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    //---------------------- LOAD INQUIRY TABLE ----------------------

    private void loadInquiryTable() {
        List<Inquiry> inquiryList = getAllInquiries();
        List<InquiryTbl> inquiryTblList = new ArrayList<>();

        for (Inquiry inquiry : inquiryList) {
            InquiryTbl inquiryTbl = new InquiryTbl(
                    inquiry.getInquiryId(),
                    getClientNameById(inquiry.getClientId()),
                    getCategoryNameById(inquiry.getCategoryId()),
                    inquiry.getSiteVisitDate(),
                    inquiry.getInquiryStatus().toString()
            );
            inquiryTblList.add(inquiryTbl);
        }

        ObservableList<InquiryTbl> obList
                = FXCollections.observableArrayList(inquiryTblList);

        inquiryTbl.setItems(obList);
    }

    private List<Inquiry> getAllInquiries() {
        List<Inquiry> inquiryList = new ArrayList<>();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT "
                    + "inquiry_id, "
                    + "client_id, "
                    + "category_id, "
                    + "inquiry_date, "
                    + "site_location, "
                    + "description, "
                    + "site_vist_date, "
                    + "status "
                    + "FROM inquiries "
                    + "ORDER BY inquiry_id DESC";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String inquiryId = String.format("IQ%03d", result.getInt("inquiry_id"));
                String clientId = result.getString("client_id");
                String categoryId = result.getString("category_id");
                String siteLocation = result.getString("site_location");
                String description = result.getString("description");
                String visitDate = result.getString("site_vist_date");
                String status = result.getString("status");

                Inquiry inquiry = new Inquiry();
                inquiry.setInquiryId(inquiryId);
                inquiry.setClientId(clientId);
                inquiry.setCategoryId(categoryId);
                inquiry.setSiteLocation(siteLocation);
                inquiry.setDescription(description);
                if (visitDate != null && !visitDate.isEmpty()) {
                    inquiry.setSiteVisitDate(LocalDate.parse(visitDate));
                }
                inquiry.setInquiryStatus(toStatus(status));

                inquiryList.add(inquiry);
            }

            result.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return inquiryList;
    }

    private InquiryStatus toStatus(String text) {
        try {
            return InquiryStatus.valueOf(text);
        } catch (IllegalArgumentException | NullPointerException e) {
            return InquiryStatus.NEW;
        }
    }

    //---------------------- GET CLIENT & CATEGORY NAME ----------------------

    private String getClientNameById(String clientId) {
        String clientName = null;
        int id = parseNumericId(clientId);
        if (id <= 0) {
            return null;
        }

        try {
            conn = DBConnection.getInstance().getConnection();
            String query = "SELECT full_name FROM clients WHERE client_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                clientName = result.getString("full_name");
            }
            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return clientName;
    }

    private String getCategoryNameById(String catId) {
        String catName = null;
        int id = parseNumericId(catId);
        if (id <= 0) {
            return null;
        }

        try {
            conn = DBConnection.getInstance().getConnection();
            String query = "SELECT category_name FROM service_categories WHERE category_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                catName = result.getString("category_name");
            }
            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return catName;
    }

    //---------------------- CLEAN INQUIRY FORM ----------------------

    @FXML
    void clickBtnClear(ActionEvent event) {
        cleanInquiryFields();
    }

    private void cleanInquiryFields() {
        searchTxtField.setText("");
        clientCombo.setValue(null);
        categoryCombo.setValue(null);
        locationTxtField.setText("");
        descriptionTxtField.setText("");
        visitDatePicker.setValue(null);
        statusCombo.setValue(null);
        inquiryTbl.getSelectionModel().clearSelection();
    }

    //---------------------- SEARCH INQUIRY ----------------------

    @FXML
    void clickBtnSearch(ActionEvent event) {
        fillFields(searchTxtField.getText().trim());
    }

    @FXML
    void pressSearchInquiry(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            fillFields(searchTxtField.getText().trim());
        }
    }

    private void fillFields(String id) {
        Inquiry inquiry = searchInquiry(id);

        if (inquiry != null) {
            int cId = parseNumericId(inquiry.getClientId());
            String cName = getClientNameById(inquiry.getClientId());
            if (cId > 0 && cName != null) {
                clientCombo.setValue(String.format("C%03d  %s", cId, cName));
            }

            int catId = parseNumericId(inquiry.getCategoryId());
            String catName = getCategoryNameById(inquiry.getCategoryId());
            if (catId > 0 && catName != null) {
                categoryCombo.setValue(String.format("SC%03d  %s", catId, catName));
            }

            locationTxtField.setText(inquiry.getSiteLocation());
            descriptionTxtField.setText(inquiry.getDescription());
            visitDatePicker.setValue(inquiry.getSiteVisitDate());
            statusCombo.setValue(inquiry.getInquiryStatus());
        }
    }

    private Inquiry searchInquiry(String inquiryId) {
        Inquiry inquiry = null;
        int inId = parseNumericId(inquiryId);
        if (inId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid Inquiry ID (e.g., IQ001)").show();
            return null;
        }

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM inquiries WHERE inquiry_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, inId);

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                String clientId = result.getString("client_id");
                String categoryId = result.getString("category_id");
                String siteLocation = result.getString("site_location");
                String description = result.getString("description");
                String visitDate = result.getString("site_vist_date");
                String status = result.getString("status");

                inquiry = new Inquiry();
                inquiry.setInquiryId(String.format("IQ%03d", inId));
                inquiry.setClientId(clientId);
                inquiry.setCategoryId(categoryId);
                inquiry.setSiteLocation(siteLocation);
                inquiry.setDescription(description);
                if (visitDate != null && !visitDate.isEmpty()) {
                    inquiry.setSiteVisitDate(LocalDate.parse(visitDate));
                }
                inquiry.setInquiryStatus(toStatus(status));
            } else {
                new Alert(Alert.AlertType.ERROR, "Inquiry not found!").show();
            }

            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return inquiry;
    }
}