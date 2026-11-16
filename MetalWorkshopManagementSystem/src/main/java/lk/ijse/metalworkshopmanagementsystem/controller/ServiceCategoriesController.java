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

public class ServiceCategoriesController implements Initializable{
    
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
        
        loadUnitsCombo();
        loadServiceCategoryTable();
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
            
            String query = "SELECT "
                    + "category_name "
                    + "FROM service_categories";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {

                String name = result.getString("category_name");

                if (name.equals(serviceCategorytModel.getCategoryName())) {

                    String updateQuery = "UPDATE service_categories SET "
                            + "is_active=true "
                            + "WHERE category_name=?";

                    PreparedStatement pstmt = conn.prepareStatement(updateQuery);

                    pstmt.setString(1, serviceCategorytModel.getCategoryName());

                    int result1 = pstmt.executeUpdate();

                    if (result1 > 0) {

                        new Alert(Alert.AlertType.INFORMATION,
                                "Service Category saved successfully!").show();

                        loadServiceCategoryTable();
                        cleanServiceCategoryFields();
                        return;

                    } else {
                        new Alert(Alert.AlertType.ERROR,
                                "Something went wrong!").show();
                    }

                    pstmt.close();
                    
                }

            }
            
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

            if (result1 > 0) {
                new Alert(Alert.AlertType.INFORMATION,
                        "Service Category saved successfully!").show();
                loadServiceCategoryTable();
                cleanServiceCategoryFields();
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
    
    //---------------------- UPDATE SERVICE CATEGORY ----------------------
    
    @FXML
    void clickBtnUpdate(ActionEvent event) {

        String catID = searchTxtField.getText().trim();
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
                    catID,
                    catName,
                    unitOfMeasure,
                    Double.parseDouble(labourRate),
                    Double.parseDouble(wastage)
            );
            
            updateCategory(serviceCategory);
        }
    }
    
    private void updateCategory(ServiceCategory serviceCategory) {

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
            pstmt.setString(5, serviceCategory.getCategoryId().substring(2));

            int result = pstmt.executeUpdate();

            if (result > 0) {

                new Alert(Alert.AlertType.INFORMATION,
                        "Service Category update successfully!").show();

                loadServiceCategoryTable();
                cleanServiceCategoryFields();

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
    
    //---------------------- DELETE SERVICE CATEGORY ----------------------
    
    @FXML
    void clickBtnDelete(ActionEvent event) {
        
        deleteCategory(searchTxtField.getText().trim());
    }
    
    private void deleteCategory(String catId) {

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "UPDATE service_categories SET "
                    + "is_active=false "
                    + "WHERE category_id=?";

            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, catId.substring(2));

            int result = pstmt.executeUpdate();

            if (result > 0) {

                new Alert(Alert.AlertType.INFORMATION,
                        "Service Category delete successfully!").show();

                loadServiceCategoryTable();
                cleanServiceCategoryFields();

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

    //---------------------- LOAD SERVICE CATEGORY TABLE ----------------------
    
    private void loadServiceCategoryTable() {
        
        List<ServiceCategory> serviceCategoryList = getAllServiceCategories();
        List<ServiceCategoryTbl> serviceCategoryTblList = new ArrayList<>();

        for (ServiceCategory serviceCategory : serviceCategoryList) {

            ServiceCategoryTbl serviceCategoryTbl = new ServiceCategoryTbl(
                    serviceCategory.getCategoryId(),
                    serviceCategory.getCategoryName(),
                    serviceCategory.getUnitOfMeasure().toString(),
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
                    + "ORDER BY category_id DESC ";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {

                String id = String.format("SC%03d", result.getInt("category_id"));
                String name = result.getString("category_name");
                String unit = result.getString("unit_of_measure");
                String labourRate = result.getString("labour_rate");
                String wastagePct = result.getString("default_wastage_pct");

                ServiceCategory serviceCategory = new ServiceCategory();
                
                serviceCategory.setCategoryId(id);
                serviceCategory.setCategoryName(name);
                serviceCategory.setUnitOfMeasure(toUnit(unit));
                serviceCategory.setLabourRate(Double.parseDouble(labourRate));
                serviceCategory.setDefaultWastagePct(Double.parseDouble(wastagePct));
                
                serviceCategoryList.add(serviceCategory);
            }

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
    }
    
    //---------------------- SEARCH SERVICE CATEGORY ----------------------
    
    @FXML
    void clickBtnSearch(ActionEvent event) {

        fillFields(searchTxtField.getText().trim());
    }

    @FXML
    void pressSearchCategory(KeyEvent event) {

        if (event.getCode() == KeyCode.ENTER) {

            fillFields(searchTxtField.getText().trim());
        }
    }
    
    private void fillFields(String id){
    
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

        try {

            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM service_categories WHERE category_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            String numId = id.substring(2);
            pstmt.setString(1, numId);

            ResultSet result = pstmt.executeQuery();

            if (result.next()) {

                String name = result.getString("category_name");
                String unit = result.getString("unit_of_measure");
                double labourRate = Double.parseDouble(result.getString("labour_rate"));
                double wastagePct = Double.parseDouble(result.getString("default_wastage_pct"));

                serviceCategory = new ServiceCategory();

                serviceCategory.setCategoryName(name);
                serviceCategory.setUnitOfMeasure(toUnit(unit));
                serviceCategory.setLabourRate(labourRate);
                serviceCategory.setDefaultWastagePct(wastagePct);

            } else {
                new Alert(Alert.AlertType.ERROR, "Service Category not found!").show();
            }

            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return serviceCategory;
    }
}
