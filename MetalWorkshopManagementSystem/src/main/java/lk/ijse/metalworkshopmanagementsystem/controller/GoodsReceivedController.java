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
import javafx.scene.control.TableCell;
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

public class GoodsReceivedController implements Initializable {

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
    private TableColumn<GoodsReceivedItemTbl, Void> removeCol;

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

        removeCol.setCellFactory(col -> new TableCell<GoodsReceivedItemTbl, Void>() {

            private final Button btn = new Button("DELETE");

            {
                btn.setStyle("-fx-background-color: #D64545; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
                btn.setOnAction(event -> {
                    GoodsReceivedItemTbl cartTbl = getTableView().getItems().get(getIndex());
                    cartTblList.remove(cartTbl);

                    totalBill = Math.max(0.0, totalBill - cartTbl.getLineCost());
                    totalLbl.setText(String.format("Rs. %.2f", totalBill));

                    loadCartTbl();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        receivedDatePicker.setValue(LocalDate.now());

        loadSupplierComboBox();
        loadUserComboBox();
        loadMaterialComboBox();

        loadGoodsReceivedTable();
        loadCartTbl();
    }

    private int parseNumericId(String raw) {
        if (raw == null) {
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

            result.close();
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

            result.close();
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
                String matCode = result.getString("material_code");
                String matName = result.getString("material_name");
                materialList.add(matCode + "  " + matName);
            }

            materialObList.addAll(materialList);
            materialCombo.setItems(materialObList);

            result.close();
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
        String qtyText = quantityTxtField.getText().trim();
        String costText = unitCostTxtField.getText().trim();

        if (material == null || material.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please select a material").show();
            return;
        }
        if (costText.isEmpty() || !costText.matches(MONEY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Cost").show();
            return;
        }
        if (qtyText.isEmpty() || !qtyText.matches(QUANTITY_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid Quantity").show();
            return;
        }

        double unitCost = Double.parseDouble(costText);
        double ordQty = Double.parseDouble(qtyText);

        if (ordQty <= 0) {
            new Alert(Alert.AlertType.ERROR, "Invalid order Qty!").show();
            return;
        }

        double lineCost = ordQty * unitCost;
        GoodsReceivedItemTbl goodsReceivedItemTbl = new GoodsReceivedItemTbl(
                material,
                ordQty,
                unitCost,
                lineCost
        );

        cartTblList.add(goodsReceivedItemTbl);
        loadCartTbl();
        cleanItemForm();

        totalBill += lineCost;
        totalLbl.setText(String.format("Rs. %.2f", totalBill));
    }

    private void loadCartTbl() {
        ObservableList<GoodsReceivedItemTbl> obCartTblList = FXCollections.observableArrayList(cartTblList);
        itemsTbl.setItems(obCartTblList);
    }

    @FXML
    void clickBtnRemoveItem(ActionEvent event) {
        GoodsReceivedItemTbl selected = itemsTbl.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartTblList.remove(selected);
            totalBill = Math.max(0.0, totalBill - selected.getLineCost());
            totalLbl.setText(String.format("Rs. %.2f", totalBill));
            loadCartTbl();
        }
    }

    //---------------------- SAVE GOODS RECEIVED ----------------------

    @FXML
    void clickBtnSave(ActionEvent event) {
        String supplier = supplierCombo.getSelectionModel().getSelectedItem();
        String user = userCombo.getSelectionModel().getSelectedItem();
        LocalDate receiveDate = receivedDatePicker.getValue();
        String invoiceRef = invoiceRefTxtField.getText().trim();

        int supplierId = parseNumericId(supplier);
        int userId = parseNumericId(user);

        if (supplier == null || supplierId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please select a supplier").show();
            return;
        }
        if (receiveDate == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a received date").show();
            return;
        }
        if (user == null || userId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please select who received the goods").show();
            return;
        }
        if (cartTblList.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please add at least one item to the delivery").show();
            return;
        }

        List<GoodsItems> goodsItemsList = new ArrayList<>();
        for (GoodsReceivedItemTbl grit : cartTblList) {
            int materialId = getMaterialId(grit.getMaterial());
            if (materialId <= 0) {
                new Alert(Alert.AlertType.ERROR, "Material not found: " + grit.getMaterial()).show();
                return;
            }
            GoodsItems gi = new GoodsItems();
            gi.setMaterialId(materialId);
            gi.setQtyReceived(grit.getQty());
            gi.setUnitCost(grit.getUnitCost());
            gi.setLineCost(grit.getLineCost());
            goodsItemsList.add(gi);
        }

        GoodsReceived goodsReceived = new GoodsReceived();
        goodsReceived.setSupplierId(supplierId);
        goodsReceived.setReceivedBy(userId);
        goodsReceived.setReceivedDateTime(receiveDate);
        goodsReceived.setInvoiceRef(invoiceRef);
        goodsReceived.setTotalAmount(totalBill);
        goodsReceived.setGoodsItemsList(goodsItemsList);

        placeOrder(goodsReceived);
    }

    //---------------------- ADD DATA TO GOODS_RECEIVED ----------------------

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
            if (result1 <= 0) {
                pstmt1.close();
                throw new SQLException("Failed to insert goods_received record");
            }

            ResultSet rs = pstmt1.getGeneratedKeys();
            if (!rs.next() || rs.getInt(1) == 0) {
                rs.close();
                pstmt1.close();
                throw new SQLException("Failed to retrieve generated GRN ID");
            }

            goodsReceived.setGrnId(rs.getInt(1));
            rs.close();
            pstmt1.close();

            addGoodsReceivedItems(conn, goodsReceived);

            conn.commit();

            loadGoodsReceivedTable();
            cleanGoodsReceivedFields();
            new Alert(Alert.AlertType.INFORMATION, "Delivery Saved successfully!").show();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            new Alert(Alert.AlertType.ERROR, "Something went wrong while saving delivery!").show();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    //---------------------- ADD DATA TO GOODS_RECEIVED_ITEMS ----------------------

    public void addGoodsReceivedItems(Connection conn, GoodsReceived goodsReceived) throws Exception {
        String query = "INSERT INTO goods_received_items("
                + "grn_id, "
                + "material_id, "
                + "quantity_received, "
                + "unit_cost, "
                + "line_total) "
                + "VALUES (?,?,?,?,?)";

        for (GoodsItems goodsItems : goodsReceived.getGoodsItemsList()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, goodsReceived.getGrnId());
            pstmt.setInt(2, goodsItems.getMaterialId());
            pstmt.setDouble(3, goodsItems.getQtyReceived());
            pstmt.setDouble(4, goodsItems.getUnitCost());
            pstmt.setDouble(5, goodsItems.getLineCost());

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result <= 0) {
                throw new SQLException("Failed to insert goods_received_items record");
            }
        }
    }

    //---------------------- LOAD GOODS RECEIVED TABLE ----------------------

    private void loadGoodsReceivedTable() {
        List<GoodsReceived> goodsReceivedList = getAllGoodsReceived();
        List<GoodsReceivedTbl> goodsReceivedTblList = new ArrayList<>();

        for (GoodsReceived goodsReceived : goodsReceivedList) {
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
        List<GoodsReceived> goodsReceivedList = new ArrayList<>();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM goods_received ORDER BY grn_id DESC";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                int grnId = result.getInt("grn_id");
                String receivedDate = result.getString("received_date");
                int supId = result.getInt("supplier_id");
                String invoiceRef = result.getString("invoice_ref");
                double totalCost = result.getDouble("total_amount");

                GoodsReceived goodsReceived = new GoodsReceived();
                goodsReceived.setGrnId(grnId);
                if (receivedDate != null && !receivedDate.isEmpty()) {
                    goodsReceived.setReceivedDateTime(LocalDate.parse(receivedDate));
                }
                goodsReceived.setSupplierId(supId);
                goodsReceived.setInvoiceRef(invoiceRef);
                goodsReceived.setTotalAmount(totalCost);

                goodsReceivedList.add(goodsReceived);
            }

            result.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return goodsReceivedList;
    }

    //---------------------- GET SUPPLIER NAME BY ID ----------------------

    private String getSupplierById(int supId) {
        String supplier = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            String query = "SELECT * FROM suppliers WHERE supplier_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, supId);

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                String id = String.format("S%03d", result.getInt("supplier_id"));
                String supName = result.getString("supplier_name");
                supplier = id + "  " + supName;
            }
            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return supplier;
    }

    //---------------------- GET MATERIAL ID BY MATERIAL CODE ----------------------

    private int getMaterialId(String materialLabel) {
        int materialId = 0;
        if (materialLabel == null || materialLabel.trim().isEmpty()) {
            return materialId;
        }
        String matCode = materialLabel.trim().split("\\s+")[0];

        try {
            conn = DBConnection.getInstance().getConnection();
            String query = "SELECT material_id FROM materials WHERE material_code=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, matCode);

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                materialId = result.getInt("material_id");
            }
            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return materialId;
    }

    @FXML
    void clickBtnClear(ActionEvent event) {
        cleanGoodsReceivedFields();
    }

    private void cleanGoodsReceivedFields() {
        supplierCombo.setValue(null);
        receivedDatePicker.setValue(LocalDate.now());
        invoiceRefTxtField.setText("");
        userCombo.setValue(null);
        cartTblList.clear();
        totalBill = 0.0;
        totalLbl.setText("Rs. 0.00");
        loadCartTbl();
        cleanItemForm();
    }

    private void cleanItemForm() {
        materialCombo.setValue(null);
        quantityTxtField.setText("");
        unitCostTxtField.setText("");
    }
}