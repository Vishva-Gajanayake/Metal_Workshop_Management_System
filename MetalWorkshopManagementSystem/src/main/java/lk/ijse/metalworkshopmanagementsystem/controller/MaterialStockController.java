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
import lk.ijse.metalworkshopmanagementsystem.model.MaterialStatus;
import lk.ijse.metalworkshopmanagementsystem.model.MaterialStock;
import lk.ijse.metalworkshopmanagementsystem.model.MaterialType;
import lk.ijse.metalworkshopmanagementsystem.model.Role;
import lk.ijse.metalworkshopmanagementsystem.model.UnitOfMaterial;
import lk.ijse.metalworkshopmanagementsystem.model.tbl.MaterialStockTbl;

public class MaterialStockController implements Initializable{

    @FXML
    private AnchorPane MaterialStockPane;

    @FXML
    private TextField codeTxtField;
    
    @FXML
    private TextField nameTxtField;

    @FXML
    private TextField qtyTxtfield;

    @FXML
    private TextField reorderLevelTxtField;

    @FXML
    private TextField searchTxtField;

    @FXML
    private TextField sizeSpecTxtField;

    @FXML
    private TextField unitCostTxtField;

    @FXML
    private TableColumn<MaterialStockTbl, String> colCode;

    @FXML
    private TableColumn<MaterialStockTbl, String> colName;

    @FXML
    private TableColumn<MaterialStockTbl, Double> colQty;

    @FXML
    private TableColumn<MaterialStockTbl, String> colStatus;

    @FXML
    private TableColumn<MaterialStockTbl, MaterialType> colType;

    @FXML
    private TableColumn<MaterialStockTbl, UnitOfMaterial> colUnit;

    @FXML
    private TableColumn<MaterialStockTbl, Double> colUnitCost;

    @FXML
    private TableView<MaterialStockTbl> materialTbl;

    @FXML
    private Button saveBtn;
    
    @FXML
    private Button updateBtn;

    @FXML
    private Button deleteBtn;
    
    @FXML
    private Button clearBtn;

    @FXML
    private ComboBox<String> supplierCombo;

    @FXML
    private ComboBox<MaterialType> typeCombo;

    @FXML
    private ComboBox<UnitOfMaterial> unitCombo;
    
    
    
    Connection conn;
    
    //---------------------- REGEX ----------------------
    
    private static final String MATERIAL_CODE_REGEX = "^[A-Z]{2}-\\d{3}$";
    private static final String MATERIAL_NAME_REGEX = "^[A-Za-z0-9 .x\"\\-]{2,100}$";
    private static final String SIZE_SPEC_REGEX = "^[A-Za-z0-9 .x\"\\-]{1,50}$";
    private static final String MONEY_REGEX = "^\\d{1,8}(\\.\\d{1,2})?$";
    private static final String QUANTITY_REGEX = "^\\d{1,6}(\\.\\d{1,3})?$";
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        colCode.setCellValueFactory(new PropertyValueFactory<>("materialCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("materialName"));
        colType.setCellValueFactory(new PropertyValueFactory<>("materialType"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unitOfMeasure"));
        colUnitCost.setCellValueFactory(new PropertyValueFactory<>("unitCost"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantityOnHand"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        loadMaterialTypeCombo();
        loadUnitCombo();
        loadSupplierComboBox();
        
        loadMaterialTable();
    }
    
    //---------------------- LOAD COMBO BOXES ----------------------
    private void loadMaterialTypeCombo() {

        typeCombo.setItems(FXCollections.observableArrayList(MaterialType.values()));

    }
    
    private void loadUnitCombo() {

        unitCombo.setItems(FXCollections.observableArrayList(UnitOfMaterial.values()));

    }
    
    private void loadSupplierComboBox() {

        List<String> supplierList = new ArrayList<>();
        ObservableList<String> supplierObList = FXCollections.observableArrayList();

        try {

            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM suppliers ORDER BY supplier_id ASC";
            Statement stmt = conn.createStatement();

            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String id = String.format("S%03d", result.getInt("supplier_id"));
                String supName = result.getString("supplier_name");
                supplierList.add(id + "  " + supName);
            }

            supplierObList.addAll(supplierList);
            supplierCombo.setItems(supplierObList);

            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }
    
    @FXML
    void clickBtnSave(ActionEvent event) {

        String materialCode = codeTxtField.getText().trim();
        String materialName = nameTxtField.getText().trim();
        MaterialType materialType = typeCombo.getSelectionModel().getSelectedItem();
        UnitOfMaterial unit = unitCombo.getSelectionModel().getSelectedItem();
        String sizeSpec = sizeSpecTxtField.getText().trim();
        double cost = Double.parseDouble(unitCostTxtField.getText().trim());
        double qty = Double.parseDouble(qtyTxtfield.getText().trim());
        double reorderLevel = Double.parseDouble(reorderLevelTxtField.getText().trim());
        String supplier = supplierCombo.getSelectionModel().getSelectedItem();
        int supId = Integer.parseInt(supplier.substring(1, 4));
        
        
        if (!materialCode.matches(MATERIAL_CODE_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Material Code").show();
        } else if(!materialName.matches(MATERIAL_NAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Material Name").show();
        }else if (materialType == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a Material Type").show();
        } else if(unit == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a Unit").show();
        }else if (!sizeSpec.matches(SIZE_SPEC_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Size Specification").show();
        } else if (!String.valueOf(cost).matches(MONEY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Cost").show();
        } else if (!String.valueOf(qty).matches(QUANTITY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Passwords not matched").show();
        } else if (!String.valueOf(reorderLevel).matches(QUANTITY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Passwords not matched").show();
        } else {

            MaterialStock materialModel = new MaterialStock();
            
            materialModel.setMaterialCode(materialCode);
            materialModel.setMaterialName(materialName);
            materialModel.setMaterialType(materialType);
            materialModel.setSupplierId(supId);
            materialModel.setQuantityOnHand(qty);
            materialModel.setReorderLevel(reorderLevel);
            materialModel.setSizeSpec(sizeSpec);
            materialModel.setUnitCost(cost);
            materialModel.setUnitOfMeasure(unit);
            materialModel.setIsActive(true);

            addMaterialStock(materialModel);
        }
    }
    
    private void addMaterialStock(MaterialStock materialModel) {

        try {

            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT "
                    + "material_code "
                    + "FROM materials";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {

                String userName = result.getString("material_code");

                if (userName.equals(materialModel.getMaterialCode())) {

                    new Alert(Alert.AlertType.INFORMATION,
                            "Material already existed!").show();

                    cleanMaterialFields();
                    return;
                }
            }

            String queryInsert = "INSERT INTO materials("
                    + "material_code, "
                    + "supplier_id, "
                    + "material_name, "
                    + "material_type, "
                    + "size_spec, "
                    + "unit_of_measure, "
                    + "unit_cost, "
                    + "quantity_on_hand, "
                    + "reorder_level, "
                    + "is_active) "
                    + "VALUES(?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(queryInsert);

            pstmt.setString(1, materialModel.getMaterialCode());
            pstmt.setInt(2, materialModel.getSupplierId());
            pstmt.setString(3, materialModel.getMaterialName());
            pstmt.setString(4, materialModel.getMaterialType().name());
            pstmt.setString(5, materialModel.getSizeSpec());
            pstmt.setString(6, materialModel.getUnitOfMeasure().name());
            pstmt.setDouble(7, materialModel.getUnitCost());
            pstmt.setDouble(8, materialModel.getQuantityOnHand());
            pstmt.setDouble(9, materialModel.getReorderLevel());
            pstmt.setObject(10, materialModel.isIsActive());
            
            int result1 = pstmt.executeUpdate();

            if (result1 > 0) {
                new Alert(Alert.AlertType.INFORMATION,
                        "Material saved successfully!").show();
                
                loadMaterialTable();
                cleanMaterialFields();
                
            } else {
                new Alert(Alert.AlertType.ERROR,
                        "Something went wrong").show();
            }

            pstmt.close();

            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Something went wrong!").show();
        }
    }
    
    //---------------------- UPDATE MATERIAL TABLE ----------------------
    
    @FXML
    void clickBtnUpdate(ActionEvent event) {

        String materialCode = codeTxtField.getText().trim();
        String materialName = nameTxtField.getText().trim();
        MaterialType materialType = typeCombo.getSelectionModel().getSelectedItem();
        UnitOfMaterial unit = unitCombo.getSelectionModel().getSelectedItem();
        String sizeSpec = sizeSpecTxtField.getText().trim();
        double cost = Double.parseDouble(unitCostTxtField.getText().trim());
        double qty = Double.parseDouble(qtyTxtfield.getText().trim());
        double reorderLevel = Double.parseDouble(reorderLevelTxtField.getText().trim());
        String supplier = supplierCombo.getSelectionModel().getSelectedItem();
        int supId = Integer.parseInt(supplier.substring(1, 4));

        if (!materialCode.matches(MATERIAL_CODE_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Material Code").show();
        } else if (!materialName.matches(MATERIAL_NAME_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Material Name").show();
        } else if (materialType == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a Material Type").show();
        } else if (unit == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a Unit").show();
        } else if (!sizeSpec.matches(SIZE_SPEC_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Size Specification").show();
        } else if (!String.valueOf(cost).matches(MONEY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Cost").show();
        } else if (!String.valueOf(qty).matches(QUANTITY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Quantity").show();
        } else if (!String.valueOf(reorderLevel).matches(QUANTITY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Re-Order Level").show();
        } else {

            MaterialStock materialModel = new MaterialStock();

            materialModel.setMaterialCode(materialCode);
            materialModel.setMaterialName(materialName);
            materialModel.setMaterialType(materialType);
            materialModel.setSupplierId(supId);
            materialModel.setQuantityOnHand(qty);
            materialModel.setReorderLevel(reorderLevel);
            materialModel.setSizeSpec(sizeSpec);
            materialModel.setUnitCost(cost);
            materialModel.setUnitOfMeasure(unit);

            updateMaterial(materialModel);
        }
    }
    
    private void updateMaterial(MaterialStock materialModel) {

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "UPDATE materials SET "
                    + "material_code=?, "
                    + "supplier_id=?, "
                    + "material_name=?, "
                    + "material_type=?, "
                    + "size_spec=?, "
                    + "unit_of_measure=?, "
                    + "unit_cost=?, "
                    + "quantity_on_hand=?, "
                    + "reorder_level=? "
                    + "WHERE material_code=?";

            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, materialModel.getMaterialCode());
            pstmt.setInt(2, materialModel.getSupplierId());
            pstmt.setString(3, materialModel.getMaterialName());
            pstmt.setString(4, materialModel.getMaterialType().name());
            pstmt.setString(5, materialModel.getSizeSpec());
            pstmt.setString(6, materialModel.getUnitOfMeasure().name());
            pstmt.setDouble(7, materialModel.getUnitCost());
            pstmt.setDouble(8, materialModel.getQuantityOnHand());
            pstmt.setDouble(9, materialModel.getReorderLevel());
            pstmt.setString(10, materialModel.getMaterialCode());

            int result = pstmt.executeUpdate();

            if (result > 0) {

                new Alert(Alert.AlertType.INFORMATION,
                        "Material update successfully!").show();

                loadMaterialTable();
                cleanMaterialFields();

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
        
        deleteMaterial(searchTxtField.getText().trim());
    }
    
    private void deleteMaterial(String materialCode) {

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "UPDATE materials SET "
                    + "is_active=false "
                    + "WHERE material_code=?";

            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, materialCode);

            int result = pstmt.executeUpdate();

            if (result > 0) {

                new Alert(Alert.AlertType.INFORMATION,
                        "Material delete successfully!").show();

                loadMaterialTable();
                cleanMaterialFields();

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
    
    //---------------------- CLEAN MATERIAL FORM ----------------------
    
    @FXML
    void clickBtnClear(ActionEvent event) {
        
        cleanMaterialFields();
    }
    
    private void cleanMaterialFields() {

        searchTxtField.setText("");
        codeTxtField.setText("");
        nameTxtField.setText("");
        unitCostTxtField.setText("");
        qtyTxtfield.setText("");
        sizeSpecTxtField.setText("");
        reorderLevelTxtField.setText("");
        supplierCombo.setValue(null);
        unitCombo.setValue(null);
        typeCombo.setValue(null);
    }
    
    //---------------------- LOAD MATERIAL TABLE ----------------------
    
    private void loadMaterialTable() {

        List<MaterialStock> materialList = getAllMaterials();
        List<MaterialStockTbl> materialTblList = new ArrayList<>();

        for (MaterialStock material : materialList) {
            
            MaterialStatus status = null;
            
            if (material.getQuantityOnHand() <= material.getReorderLevel()) {
                status = MaterialStatus.LOW;
            }else {
                status = MaterialStatus.OKAY;
            }

            MaterialStockTbl materialTbl = new MaterialStockTbl(
                    
                    material.getMaterialCode(),
                    material.getMaterialName(),
                    material.getMaterialType(),
                    material.getUnitOfMeasure(),
                    material.getUnitCost(),
                    material.getQuantityOnHand(),
                    status
            );

            materialTblList.add(materialTbl);
        }

        ObservableList<MaterialStockTbl> obList
                = FXCollections.observableArrayList(materialTblList);

        materialTbl.setItems(obList);
    }
    
    private List<MaterialStock> getAllMaterials() {

        List<MaterialStock> matereialist = new ArrayList<>();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT "
                    + "* "
                    + "FROM materials "
                    + "WHERE is_active = '1' "
                    + "ORDER BY material_id DESC ";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {

                String code = result.getString("material_code");
                String name = result.getString("material_name");
                String type = result.getString("material_type");
                String unit = result.getString("unit_of_measure");
                String cost = result.getString("unit_cost");
                String qty = result.getString("quantity_on_hand");
                String reorderLevel = result.getString("reorder_level");

                MaterialStock material = new MaterialStock();

                material.setMaterialCode(code);
                material.setMaterialName(name);
                material.setMaterialType(toType(type));
                material.setUnitOfMeasure(toUnit(unit));
                material.setUnitCost(Double.parseDouble(cost));
                material.setQuantityOnHand(Double.parseDouble(qty));
                material.setReorderLevel(Double.parseDouble(reorderLevel));

                matereialist.add(material);
            }

            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return matereialist;
    }
    
    private MaterialType toType(String text) {
        try {
            return MaterialType.valueOf(text);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
    
    private UnitOfMaterial toUnit(String text) {
        try {
            return UnitOfMaterial.valueOf(text);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
    
    //---------------------- SEARCH MATERIAL ----------------------

    @FXML
    void pressSearchMaterial(KeyEvent event) {

        if (event.getCode() == KeyCode.ENTER) {

            fillFields(searchTxtField.getText().trim());
        }
    }
    
    private void fillFields(String code) {

        MaterialStock materialModel = searchMaterial(code);

        if (materialModel != null) {
            codeTxtField.setText(materialModel.getMaterialCode());
            nameTxtField.setText(materialModel.getMaterialName());
            typeCombo.setValue(materialModel.getMaterialType());
            unitCombo.setValue(materialModel.getUnitOfMeasure());
            sizeSpecTxtField.setText(materialModel.getSizeSpec());
            unitCostTxtField.setText(String.valueOf(materialModel.getUnitCost()));
            qtyTxtfield.setText(String.valueOf(materialModel.getQuantityOnHand()));
            reorderLevelTxtField.setText(String.valueOf(materialModel.getReorderLevel()));
            supplierCombo.setValue(getSupplierById(materialModel.getSupplierId()));
        }
    }
    
    private MaterialStock searchMaterial(String materialCode) {

        MaterialStock materialModel = null;

        try {

            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM materials WHERE material_code=?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, materialCode);

            ResultSet result = pstmt.executeQuery();

            if (result.next()) {

                String code = result.getString("material_code");
                String name = result.getString("material_name");
                String type = result.getString("material_type");
                String unit = result.getString("unit_of_measure");
                String sizeSpec = result.getString("size_spec");
                double cost = result.getDouble("unit_cost");
                double qty = result.getDouble("quantity_on_hand");
                double reorderLevel = result.getDouble("reorder_level");
                int supId = result.getInt("supplier_id");

                materialModel = new MaterialStock();

                materialModel.setMaterialCode(code);
                materialModel.setMaterialName(name);
                materialModel.setMaterialType(toType(type));
                materialModel.setUnitOfMeasure(toUnit(unit));
                materialModel.setSizeSpec(sizeSpec);
                materialModel.setUnitCost(cost);
                materialModel.setQuantityOnHand(qty);
                materialModel.setReorderLevel(reorderLevel);
                materialModel.setSupplierId(supId);

            } else {
                new Alert(Alert.AlertType.ERROR, "Material not found!").show();
            }

            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return materialModel;
    }
    
    private String getSupplierById(int supId) {

        String supplier = null;

        try {

            String query = "SELECT * FROM suppliers WHERE supplier_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, String.valueOf(supId));

            ResultSet result = pstmt.executeQuery();

            while (result.next()) {
                String id = String.format("S%03d", result.getInt("supplier_id"));
                String userName = result.getString("supplier_name");
                supplier = id + "  " + userName;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return supplier;
    }
    
}
