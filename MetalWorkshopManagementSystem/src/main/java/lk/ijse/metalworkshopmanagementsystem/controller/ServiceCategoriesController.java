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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.metalworkshopmanagementsystem.db.DBConnection;
import lk.ijse.metalworkshopmanagementsystem.model.ServiceCategory;
import lk.ijse.metalworkshopmanagementsystem.model.UnitOfMeasure;
import lk.ijse.metalworkshopmanagementsystem.model.tbl.ServiceCategoryTbl;

public class ServiceCategoriesController implements Initializable {

    Connection conn;

    @FXML
    private AnchorPane serviceCategoriesPane;

    @FXML
    private TextField catNameTxtField;

    @FXML
    private TextField labourRateTxtField;

    @FXML
    private TextField wastageTxtField;

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
    private TableView<ServiceCategoryTbl> catTbl;

    @FXML
    private TableColumn<ServiceCategoryTbl, String> colCategory;

    @FXML
    private TableColumn<ServiceCategoryTbl, String> colId;

    @FXML
    private TableColumn<ServiceCategoryTbl, String> colLabourRate;

    @FXML
    private TableColumn<ServiceCategoryTbl, String> colUnit;

    @FXML
    private TableColumn<ServiceCategoryTbl, String> colWastage;

    @FXML
    private ComboBox<UnitOfMeasure> unitCombo;

    //---------------------- REGEX ----------------------

    private static final String CATEGORY_NAME_REGEX = "^[A-Za-z]+(?:[\\s&][A-Za-z]+)*$";
    private static final String MONEY_REGEX = "^\\d{1,8}(\\.\\d{1,2})?$";
    private static final String PERCENT_REGEX = "^(100(\\.0{1,2})?|\\d{1,2}(\\.\\d{1,2})?)$";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unitOfMeasure"));
        colLabourRate.setCellValueFactory(new PropertyValueFactory<>("labourRate"));
        colWastage.setCellValueFactory(new PropertyValueFactory<>("defaultWastagePct"));

        catTbl.setOnMouseClicked(event -> {
            ServiceCategoryTbl selected = catTbl.getSelectionModel().getSelectedItem();
            if (selected != null) {
                searchTxtField.setText(selected.getCategoryId());
                fillFields(selected.getCategoryId());
            }
        });

        loadUnitsCombo();
        loadServiceCategoryTable();
    }

    private int parseCategoryId(String rawId) {
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

    private void loadUnitsCombo() {
        unitCombo.setItems(FXCollections.observableArrayList(UnitOfMeasure.values()));
    }

    //---------------------- SAVE SERVICE CATEGORY ----------------------

    @FXML
    void clickBtnSave(ActionEvent event) {
        String catName = catNameTxtField.getText().trim();
        UnitOfMeasure unitOfMeasure = unitCombo.getSelectionModel().getSelectedItem();
        String labourRate = labourRateTxtField.getText().trim();
        String wastage = wastageTxtField.getText().trim();

        if (!catName.matches(CATEGORY_NAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Service Category Name").show();
        } else if (unitOfMeasure == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a unit of measure").show();
        } else if (!labourRate.matches(MONEY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid labour Rate").show();
        } else if (!wastage.matches(PERCENT_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Wastage Percentage").show();
        } else {
            ServiceCategory serviceCategorytModel = new ServiceCategory(
                    catName,
                    unitOfMeasure,
                    Double.parseDouble(labourRate),
                    Double.parseDouble(wastage),
                    true
            );

            addServiceCategory(serviceCategorytModel);
        }
    }

    private void addServiceCategory(ServiceCategory serviceCategorytModel) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String checkQuery = "SELECT category_id FROM service_categories WHERE category_name=?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, serviceCategorytModel.getCategoryName());
            ResultSet result = checkStmt.executeQuery();

            if (result.next()) {
                result.close();
                checkStmt.close();

                String updateQuery = "UPDATE service_categories SET "
                        + "unit_of_measure=?, "
                        + "labour_rate=?, "
                        + "default_wastage_pct=?, "
                        + "is_active=true "
                        + "WHERE category_name=?";

                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, serviceCategorytModel.getUnitOfMeasure().name());
                updateStmt.setDouble(2, serviceCategorytModel.getLabourRate());
                updateStmt.setDouble(3, serviceCategorytModel.getDefaultWastagePct());
                updateStmt.setString(4, serviceCategorytModel.getCategoryName());

                int result1 = updateStmt.executeUpdate();
                updateStmt.close();

                if (result1 > 0) {
                    new Alert(Alert.AlertType.INFORMATION, "Service Category saved successfully!").show();
                    loadServiceCategoryTable();
                    cleanServiceCategoryFields();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
                }
                return;
            }
            result.close();
            checkStmt.close();

            String queryInsert = "INSERT INTO service_categories("
                    + "category_name, "
                    + "unit_of_measure, "
                    + "labour_rate, "
                    + "default_wastage_pct, "
                    + "is_active) "
                    + "VALUES(?,?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(queryInsert);

            pstmt.setString(1, serviceCategorytModel.getCategoryName());
            pstmt.setString(2, serviceCategorytModel.getUnitOfMeasure().name());
            pstmt.setDouble(3, serviceCategorytModel.getLabourRate());
            pstmt.setDouble(4, serviceCategorytModel.getDefaultWastagePct());
            pstmt.setObject(5, serviceCategorytModel.isIsActive());

            int result1 = pstmt.executeUpdate();
            pstmt.close();

            if (result1 > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Service Category saved successfully!").show();
                loadServiceCategoryTable();
                cleanServiceCategoryFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    //---------------------- UPDATE SERVICE CATEGORY ----------------------

    @FXML
    void clickBtnUpdate(ActionEvent event) {
        String catIDRaw = searchTxtField.getText().trim();
        int numericId = parseCategoryId(catIDRaw);

        if (numericId <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please search or select a valid Category ID first!").show();
            return;
        }

        String catName = catNameTxtField.getText().trim();
        UnitOfMeasure unitOfMeasure = unitCombo.getSelectionModel().getSelectedItem();
        String labourRate = labourRateTxtField.getText().trim();
        String wastage = wastageTxtField.getText().trim();

        if (!catName.matches(CATEGORY_NAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Service Category Name").show();
        } else if (unitOfMeasure == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a unit of measure").show();
        } else if (!labourRate.matches(MONEY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid labour Rate").show();
        } else if (!wastage.matches(PERCENT_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Wastage Percentage").show();
        } else {
            ServiceCategory serviceCategory = new ServiceCategory(
                    String.format("SC%03d", numericId),
                    catName,
                    unitOfMeasure,
                    Double.parseDouble(labourRate),
                    Double.parseDouble(wastage)
            );

            updateCategory(serviceCategory, numericId);
        }
    }

    private void updateCategory(ServiceCategory serviceCategory, int numericId) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "UPDATE service_categories SET "
                    + "category_name=?, "
                    + "unit_of_measure=?, "
                    + "labour_rate=?, "
                    + "default_wastage_pct=? "
                    + "WHERE category_id=?";

            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, serviceCategory.getCategoryName());
            pstmt.setString(2, serviceCategory.getUnitOfMeasure().name());
            pstmt.setDouble(3, serviceCategory.getLabourRate());
            pstmt.setDouble(4, serviceCategory.getDefaultWastagePct());
            pstmt.setInt(5, numericId);

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Service Category updated successfully!").show();
                loadServiceCategoryTable();
                cleanServiceCategoryFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Service Category not found or not updated!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    //---------------------- DELETE SERVICE CATEGORY ----------------------

    @FXML
    void clickBtnDelete(ActionEvent event) {
        int numericId = parseCategoryId(searchTxtField.getText());
        if (numericId <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please search or select a valid Category ID first!").show();
            return;
        }
        deleteCategory(numericId);
    }

    private void deleteCategory(int numericId) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "UPDATE service_categories SET is_active=false WHERE category_id=?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, numericId);

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Service Category deleted successfully!").show();
                loadServiceCategoryTable();
                cleanServiceCategoryFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Service Category not found!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    //---------------------- LOAD SERVICE CATEGORY TABLE ----------------------

    private void loadServiceCategoryTable() {
        List<ServiceCategory> serviceCategoryList = getAllServiceCategories();
        List<ServiceCategoryTbl> serviceCategoryTblList = new ArrayList<>();

        for (ServiceCategory serviceCategory : serviceCategoryList) {
            String unitText = (serviceCategory.getUnitOfMeasure() != null)
                    ? serviceCategory.getUnitOfMeasure().toString()
                    : "";

            ServiceCategoryTbl serviceCategoryTbl = new ServiceCategoryTbl(
                    serviceCategory.getCategoryId(),
                    serviceCategory.getCategoryName(),
                    unitText,
                    serviceCategory.getLabourRate(),
                    serviceCategory.getDefaultWastagePct()
            );

            serviceCategoryTblList.add(serviceCategoryTbl);
        }

        ObservableList<ServiceCategoryTbl> obList
                = FXCollections.observableArrayList(serviceCategoryTblList);

        catTbl.setItems(obList);
    }

    private List<ServiceCategory> getAllServiceCategories() {
        List<ServiceCategory> serviceCategoryList = new ArrayList<>();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT "
                    + "category_id, "
                    + "category_name, "
                    + "unit_of_measure, "
                    + "labour_rate, "
                    + "default_wastage_pct "
                    + "FROM service_categories "
                    + "WHERE is_active = '1' "
                    + "ORDER BY category_id DESC";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String id = String.format("SC%03d", result.getInt("category_id"));
                String name = result.getString("category_name");
                String unit = result.getString("unit_of_measure");
                double labourRate = result.getDouble("labour_rate");
                double wastagePct = result.getDouble("default_wastage_pct");

                ServiceCategory serviceCategory = new ServiceCategory();
                serviceCategory.setCategoryId(id);
                serviceCategory.setCategoryName(name);
                serviceCategory.setUnitOfMeasure(toUnit(unit));
                serviceCategory.setLabourRate(labourRate);
                serviceCategory.setDefaultWastagePct(wastagePct);

                serviceCategoryList.add(serviceCategory);
            }

            result.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return serviceCategoryList;
    }

    private UnitOfMeasure toUnit(String text) {
        try {
            return UnitOfMeasure.valueOf(text);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    //---------------------- CLEAN SERVICE CATEGORY FORM ----------------------

    @FXML
    void clickBtnClear(ActionEvent event) {
        cleanServiceCategoryFields();
    }

    private void cleanServiceCategoryFields() {
        searchTxtField.setText("");
        catNameTxtField.setText("");
        unitCombo.setValue(null);
        labourRateTxtField.setText("");
        wastageTxtField.setText("");
        catTbl.getSelectionModel().clearSelection();
    }

    //---------------------- SEARCH SERVICE CATEGORY ----------------------

    @FXML
    void clickBtnSearch(ActionEvent event) {
        performSearch();
    }

    @FXML
    void pressSearchCategory(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            performSearch();
        }
    }

    private void performSearch() {
        int numericId = parseCategoryId(searchTxtField.getText());
        if (numericId <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please enter a valid Category ID (e.g., SC001 or 1)").show();
            return;
        }
        fillFields(String.valueOf(numericId));
    }

    private void fillFields(String id) {
        ServiceCategory serviceCategory = searchServiceCategory(id);

        if (serviceCategory != null) {
            catNameTxtField.setText(serviceCategory.getCategoryName());
            unitCombo.setValue(serviceCategory.getUnitOfMeasure());
            labourRateTxtField.setText(String.valueOf(serviceCategory.getLabourRate()));
            wastageTxtField.setText(String.valueOf(serviceCategory.getDefaultWastagePct()));
        }
    }

    private ServiceCategory searchServiceCategory(String id) {
        ServiceCategory serviceCategory = null;
        int numId = parseCategoryId(id);

        if (numId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Invalid Category ID!").show();
            return null;
        }

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM service_categories WHERE category_id=? AND is_active = '1'";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, numId);

            ResultSet result = pstmt.executeQuery();

            if (result.next()) {
                String name = result.getString("category_name");
                String unit = result.getString("unit_of_measure");
                double labourRate = result.getDouble("labour_rate");
                double wastagePct = result.getDouble("default_wastage_pct");

                serviceCategory = new ServiceCategory();
                serviceCategory.setCategoryName(name);
                serviceCategory.setUnitOfMeasure(toUnit(unit));
                serviceCategory.setLabourRate(labourRate);
                serviceCategory.setDefaultWastagePct(wastagePct);
            } else {
                new Alert(Alert.AlertType.ERROR, "Service Category not found!").show();
            }

            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return serviceCategory;
    }
}