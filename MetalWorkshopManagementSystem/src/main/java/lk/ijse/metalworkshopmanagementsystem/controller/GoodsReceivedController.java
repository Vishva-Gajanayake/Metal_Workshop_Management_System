package lk.ijse.metalworkshopmanagementsystem.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lk.ijse.metalworkshopmanagementsystem.db.DBConnection;
import lk.ijse.metalworkshopmanagementsystem.model.GoodsItems;
import lk.ijse.metalworkshopmanagementsystem.model.GoodsReceived;
import lk.ijse.metalworkshopmanagementsystem.model.tbl.GoodsReceivedItemTbl;
import lk.ijse.metalworkshopmanagementsystem.model.tbl.GoodsReceivedTbl;

public class GoodsReceivedController implements Initializable{

    @FXML
    private AnchorPane goodsReceivedPane;

    @FXML
    private ComboBox<String> supplierCombo;        
    @FXML
    private DatePicker receivedDatePicker;
    @FXML
    private TextField invoiceRefTxtField;
    @FXML
    private ComboBox<String> userCombo;

    @FXML
    private ComboBox<String> materialCombo;  
    
    @FXML
    private TextField quantityTxtField;
    @FXML
    private TextField unitCostTxtField;
    @FXML
    private Button addItemBtn;
    @FXML
    private Button removeItemBtn;

    @FXML
    private TableView<GoodsReceivedItemTbl> itemsTbl;
    @FXML
    private TableColumn<GoodsReceivedItemTbl, String> colItemMaterial;
    @FXML
    private TableColumn<GoodsReceivedItemTbl, Double> colItemQty;
    @FXML
    private TableColumn<GoodsReceivedItemTbl, Double> colItemUnitCost;
    @FXML
    private TableColumn<GoodsReceivedItemTbl, Double> colItemLineTotal;

    @FXML
    private Label totalLbl;
    @FXML
    private Button saveBtn;
    @FXML
    private Button clearBtn;

    @FXML
    private TableView<GoodsReceivedTbl> historyTbl;
    @FXML
    private TableColumn<GoodsReceivedTbl, String> colGrnId;
    @FXML
    private TableColumn<GoodsReceivedTbl, String> colGrnDate;
    @FXML
    private TableColumn<GoodsReceivedTbl, String> colGrnSupplier;
    @FXML
    private TableColumn<GoodsReceivedTbl, String> colGrnInvoice;
    @FXML
    private TableColumn<GoodsReceivedTbl, Double> colGrnTotal;
    
    Connection conn;
    
    private double totalBill = 0.0;
    
    private List<GoodsReceivedItemTbl> cartTblList = new ArrayList<>();
    
    //---------------------- REGEX ----------------------
    
    private static final String QUANTITY_REGEX = "^\\d{1,6}(\\.\\d{1,3})?$";
    private static final String MONEY_REGEX = "^\\d{1,8}(\\.\\d{1,2})?$";
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        colItemMaterial.setCellValueFactory(new PropertyValueFactory<>("material"));
        colItemQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colItemUnitCost.setCellValueFactory(new PropertyValueFactory<>("unitCost"));
        colItemLineTotal.setCellValueFactory(new PropertyValueFactory<>("lineCost"));
        
        colGrnId.setCellValueFactory(new PropertyValueFactory<>("grnId"));
        colGrnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colGrnSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        colGrnInvoice.setCellValueFactory(new PropertyValueFactory<>("inoviceRef"));
        colGrnTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
     
        loadSupplierComboBox();
        loadUserComboBox();
        loadMaterialComboBox();
        
        loadGoodsReceivedTable();
        loadCartTbl();
    }
    
    //---------------------- LOAD COMBO BOXES ----------------------
    
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
    
    private void loadUserComboBox() {

        List<String> userList = new ArrayList<>();
        ObservableList<String> userObList = FXCollections.observableArrayList();

        try {

            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM users ORDER BY user_id ASC";
            Statement stmt = conn.createStatement();

            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String id = String.format("U%03d", result.getInt("user_id"));
                String userName = result.getString("username");
                userList.add(id + "  " + userName);
            }

            userObList.addAll(userList);
            userCombo.setItems(userObList);

            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }
    
    private void loadMaterialComboBox() {

        List<String> materialList = new ArrayList<>();
        ObservableList<String> materialObList = FXCollections.observableArrayList();

        try {

            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM materials ORDER BY material_id ASC";
            Statement stmt = conn.createStatement();

            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
//                String id = String.format("M%03d", result.getInt("material_id"));
                String matCode = result.getString("material_code");
                String matName = result.getString("material_name");
                materialList.add(matCode + "  " + matName);
            }

            materialObList.addAll(materialList);
            materialCombo.setItems(materialObList);

            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }
    
    //---------------------- ADD ITEMS TO CART ----------------------
    
    @FXML
    void clickBtnAddItem(ActionEvent event) {
        
        String material = materialCombo.getSelectionModel().getSelectedItem();
        String qty = quantityTxtField.getText().trim();
        double unitCost = Double.parseDouble(unitCostTxtField.getText().trim());

        if (!String.valueOf(unitCost).matches(MONEY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Cost").show();
        } else if (!qty.matches(QUANTITY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Quantity").show();
        }
        else {

            double ordQty = Double.parseDouble(qty);

            if (!(ordQty < 1)) {
                GoodsReceivedItemTbl goodsReceivedItemTbl = new GoodsReceivedItemTbl(
                        material,
                        ordQty,
                        unitCost,
                        ordQty * unitCost
                );

                cartTblList.add(goodsReceivedItemTbl);
                loadCartTbl();
//                cleanItemForm();

                totalBill = totalBill + (unitCost * ordQty);
                totalLbl.setText(String.format("Rs.%.2f", totalBill));

            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid order Qty!").show();
            }
        }
    }
    
    private void loadCartTbl() {

        ObservableList<GoodsReceivedItemTbl> obCartTblList = FXCollections.observableArrayList(cartTblList);
        itemsTbl.setItems(obCartTblList);

    }

    @FXML
    void clickBtnRemoveItem(ActionEvent event) {
        
    }
    
    //---------------------- SAVE GOODS RECEIVED ----------------------

    @FXML
    void clickBtnSave(ActionEvent event) {
        
        String supplier = supplierCombo.getSelectionModel().getSelectedItem();
        String user = userCombo.getSelectionModel().getSelectedItem();
        LocalDate receiveDate = receivedDatePicker.getValue();
        String invoiceRef = invoiceRefTxtField.getText().trim();
        List<GoodsItems> goodsItemsList = new ArrayList<>();

        for (GoodsReceivedItemTbl grit : cartTblList) {
            
            GoodsItems gi = new GoodsItems();
            
            gi.setMaterialId(getMaterialId(grit.getMaterial()));
            gi.setQtyReceived(grit.getQty());
            gi.setUnitCost(grit.getUnitCost());
            gi.setLineCost(grit.getLineCost());

            goodsItemsList.add(gi);
        }

        GoodsReceived goodsReceived = new GoodsReceived();
        goodsReceived.setSupplierId(Integer.parseInt(supplier.substring(1, 4)));
        goodsReceived.setReceivedBy(Integer.parseInt(user.substring(1, 4)));
        goodsReceived.setReceivedDateTime(receiveDate);
        goodsReceived.setInvoiceRef(invoiceRef);
        goodsReceived.setTotalAmount(totalBill);
        goodsReceived.setGoodsItemsList(goodsItemsList);

        placeOrder(goodsReceived);
    
    }
    
    //---------------------- ADD DATA TO GODDS_RECEIVED ----------------------
    
    public void placeOrder(GoodsReceived goodsReceived) {

        try {
            conn = DBConnection.getInstance().getConnection();

            conn.setAutoCommit(false);

            String query1 = "INSERT INTO goods_received("
                    + "supplier_id, "
                    + "received_by, "
                    + "received_date, "
                    + "invoice_ref, "
                    + "total_amount) "
                    + "VALUES(?,?,?,?,?)";
            PreparedStatement pstmt1 = conn.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);

            pstmt1.setInt(1, goodsReceived.getSupplierId());
            pstmt1.setInt(2, goodsReceived.getReceivedBy());
            pstmt1.setObject(3, goodsReceived.getReceivedDateTime());
            pstmt1.setString(4, goodsReceived.getInvoiceRef());
            pstmt1.setDouble(5, goodsReceived.getTotalAmount());

            int result1 = pstmt1.executeUpdate();

            if (result1 > 0) {

                ResultSet rs = pstmt1.getGeneratedKeys();

                if (rs.next()) {

                    if (rs.getInt(1) != 0) {

                        goodsReceived.setGrnId(rs.getInt(1));
                        addGoodsReceivedItems(conn, goodsReceived);

                        loadGoodsReceivedTable();
//                        cleanFullOrderForm();

                        new Alert(Alert.AlertType.INFORMATION, "Delivery Saved successfully!").show();

                    } else {
                        throw new SQLException("Something went wrong!");
                    }

                } else {
                    throw new SQLException("Something went wrong!");
                }

            } else {
                throw new SQLException("Something went wrong!");
            }

            try {
                conn.commit();

//                printInvoice(order.getOrderId());

            } catch (Exception e) {
                e.getStackTrace();
                System.out.println(e.getMessage());
            }

        } catch (Exception e) {

            try {
                conn.rollback();
            } catch (SQLException ex) {
                e.getStackTrace();
                System.out.println(e.getMessage());
            }
            e.getStackTrace();
            System.out.println(e.getMessage());
        }
    }
    
    //---------------------- ADD DATA TO GODDS_RECEIVED_TEM ----------------------
    
    public void addGoodsReceivedItems(Connection conn, GoodsReceived goodsReceived) throws Exception {

        for (GoodsItems goodsItems : goodsReceived.getGoodsItemsList()) {

            String query = "INSERT INTO goods_received_items(grn_id, material_id, quantity_received, unit_cost, line_total) VALUES (?, ?, ?, ?,?)";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, goodsReceived.getGrnId());
            pstmt.setInt(2, goodsItems.getMaterialId());
            pstmt.setDouble(3, goodsItems.getQtyReceived());
            pstmt.setDouble(4, goodsItems.getUnitCost());
            pstmt.setDouble(5, goodsItems.getLineCost());

            int result = pstmt.executeUpdate();

            if (result > 0) {

                System.out.println("Goods Received Items saved successfully!");

            } else {
                throw new SQLException("Something went wrong!");
            }
        }
    }
    
    //---------------------- LOAD GOODS RECEIVED TABLE ----------------------
    
    private void loadGoodsReceivedTable() {

        List<GoodsReceived> goodsReceivedlList = getAllGoodsReceived();
        List<GoodsReceivedTbl> goodsReceivedTblList = new ArrayList<>();

        for (GoodsReceived goodsReceived : goodsReceivedlList) {

            GoodsReceivedTbl goodsReceivedTbl = new GoodsReceivedTbl(
                    goodsReceived.getGrnId(),
                    goodsReceived.getReceivedDateTime(),
                    getSupplierById(goodsReceived.getSupplierId()),
                    goodsReceived.getInvoiceRef(),
                    goodsReceived.getTotalAmount()
            );

            goodsReceivedTblList.add(goodsReceivedTbl);
        }

        ObservableList<GoodsReceivedTbl> obList
                = FXCollections.observableArrayList(goodsReceivedTblList);

        historyTbl.setItems(obList);
    }
    
    private List<GoodsReceived> getAllGoodsReceived() {

        List<GoodsReceived> goodsReceivedlist = new ArrayList<>();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT "
                    + "* "
                    + "FROM goods_received "
                    + "ORDER BY grn_id DESC ";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {

                int grnId = result.getInt("grn_id");
                String receivedDate = result.getString("received_date");
                int supId = result.getInt("supplier_id");
                String invoiceRef = result.getString("invoice_ref");
                String totalCost = result.getString("total_amount");

                GoodsReceived goodsReceived = new GoodsReceived();

                goodsReceived.setGrnId(grnId);
                goodsReceived.setReceivedDateTime(LocalDate.parse(receivedDate));
                goodsReceived.setSupplierId(supId);
                goodsReceived.setInvoiceRef(invoiceRef);
                goodsReceived.setTotalAmount(Double.parseDouble(totalCost));

                goodsReceivedlist.add(goodsReceived);
            }

            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return goodsReceivedlist;
    }
    
    //---------------------- GET SUPPLIER NAME BY ID ----------------------
    
    private String getSupplierById(int supId) {

        String supplier = null;

        try {

            String query = "SELECT * FROM suppliers WHERE supplier_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, String.valueOf(supId));

            ResultSet result = pstmt.executeQuery();

            while (result.next()) {
                String id = String.format("S%03d", result.getInt("supplier_id"));
                String supName = result.getString("supplier_name");
                supplier = id + "  " + supName;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return supplier;
    }
    
    //---------------------- GET MATERIAL ID BY MATERIAL CODE ----------------------
    
    private int getMaterialId(String materialCode) {

        int materialId = 0;

        try {

            String query = "SELECT material_id FROM materials WHERE material_code=?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, materialCode);

            ResultSet result = pstmt.executeQuery();

            while (result.next()) {
                materialId = result.getInt("material_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return materialId;
    }

    @FXML
    void clickBtnClear(ActionEvent event) {
    
    }
    
}
