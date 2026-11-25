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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import lk.ijse.metalworkshopmanagementsystem.db.DBConnection;
import lk.ijse.metalworkshopmanagementsystem.model.Estimate;

public class EstimatorController implements Initializable {

    @FXML
    private Button calculateBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private AnchorPane estimatorPane;

    @FXML
    private TextField heightTxtField;

    @FXML
    private ComboBox<String> inquiryCombo;

    @FXML
    private TextField labourTxtField;

    @FXML
    private TextField lengthTxtField;

    @FXML
    private TextField materialCostTxtField;

    @FXML
    private TextField overheadTxtField;

    @FXML
    private TextField quantityTxtField;

    @FXML
    private Button saveBtn;

    @FXML
    private TextField totalTxtField;

    @FXML
    private TextField wastageTxtField;

    @FXML
    private TextField widthTxtField;

    Connection conn;

    //---------------------- REGEX ----------------------
    private static final String DIMENSION_REGEX = "^\\d{1,3}(\\.\\d{1,2})?$";
    private static final String QUANTITY_REGEX = "^\\d{1,6}(\\.\\d{1,3})?$";
    private static final String MONEY_REGEX = "^\\d{1,8}(\\.\\d{1,2})?$";
    private static final String PERCENT_REGEX = "^(100(\\.0{1,2})?|\\d{1,2}(\\.\\d{1,2})?)$";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadInquiryComboBox();
    }

    private int parseNumericId(String raw) {
        if (raw == null) {
            return -1;
        }
        String digits = raw.trim().replaceAll("\\D+", "");
        if (digits.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private Double parseValidDouble(String rawText, String regex, String errorMessage) {
        String text = (rawText == null) ? "" : rawText.trim();
        if (text.isEmpty() || !text.matches(regex)) {
            new Alert(Alert.AlertType.ERROR, errorMessage).show();
            return null;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, errorMessage).show();
            return null;
        }
    }

    //---------------------- LOAD COMBO BOXES ----------------------

    private void loadInquiryComboBox() {
        List<String> inqList = new ArrayList<>();
        ObservableList<String> inqObList = FXCollections.observableArrayList();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM inquiries ORDER BY inquiry_id ASC";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String id = String.format("I%03d", result.getInt("inquiry_id"));
                inqList.add(id);
            }

            inqObList.addAll(inqList);
            inquiryCombo.setItems(inqObList);

            result.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    private static class CalculationResult {
        final int inquiryId;
        final double width;
        final double height;
        final double length;
        final double qty;
        final double wastage;
        final double rawMaterialCost;
        final double materialWithWastage;
        final double overhead;
        final double labourCost;
        final double totalCost;

        CalculationResult(int inquiryId, double width, double height, double length, double qty,
                          double wastage, double rawMaterialCost, double materialWithWastage,
                          double overhead, double labourCost, double totalCost) {
            this.inquiryId = inquiryId;
            this.width = width;
            this.height = height;
            this.length = length;
            this.qty = qty;
            this.wastage = wastage;
            this.rawMaterialCost = rawMaterialCost;
            this.materialWithWastage = materialWithWastage;
            this.overhead = overhead;
            this.labourCost = labourCost;
            this.totalCost = totalCost;
        }
    }

    private CalculationResult computeEstimate() {
        String inquirySelection = inquiryCombo.getValue();
        int inquiryId = parseNumericId(inquirySelection);
        if (inquirySelection == null || inquiryId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please select an inquiry").show();
            return null;
        }

        Double width = parseValidDouble(widthTxtField.getText(), DIMENSION_REGEX,
                "Invalid width (type 0 if not used)");
        if (width == null) return null;

        Double height = parseValidDouble(heightTxtField.getText(), DIMENSION_REGEX,
                "Invalid height (type 0 if not used)");
        if (height == null) return null;

        Double length = parseValidDouble(lengthTxtField.getText(), DIMENSION_REGEX,
                "Invalid length (type 0 if not used)");
        if (length == null) return null;

        Double qty = parseValidDouble(quantityTxtField.getText(), QUANTITY_REGEX,
                "Invalid quantity");
        if (qty == null) return null;

        Double wastage = parseValidDouble(wastageTxtField.getText(), PERCENT_REGEX,
                "Invalid wastage %");
        if (wastage == null) return null;

        Double materialCost = parseValidDouble(materialCostTxtField.getText(), MONEY_REGEX,
                "Invalid material cost");
        if (materialCost == null) return null;

        Double overhead = parseValidDouble(overheadTxtField.getText(), MONEY_REGEX,
                "Invalid overhead");
        if (overhead == null) return null;

        double effectiveLength = (length == 0.0) ? 1.0 : length;
        double size = width * height * effectiveLength * qty;
        double labour = size * getLabourRate(inquiryId);
        double material = materialCost * (1.0 + (wastage / 100.0));
        double total = material + labour + overhead;

        return new CalculationResult(
                inquiryId, width, height, effectiveLength, qty,
                wastage, materialCost, material, overhead, labour, total
        );
    }

    @FXML
    void clickBtnCalculate(ActionEvent event) {
        CalculationResult calc = computeEstimate();
        if (calc == null) {
            return;
        }

        labourTxtField.setText(String.format("%.2f", calc.labourCost));
        totalTxtField.setText(String.format("%.2f", calc.totalCost));
    }

    private double getLabourRate(int inquiryId) {
        double labourRate = 0.0;

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT sc.labour_rate FROM inquiries i "
                    + "JOIN service_categories sc ON i.category_id = sc.category_id "
                    + "WHERE i.inquiry_id=?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, inquiryId);

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                labourRate = result.getDouble("labour_rate");
            }
            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return labourRate;
    }

    private int resolveCreatedByUserId() {
        if (LoginController.loggedUser != null && LoginController.loggedUser.getUserId() != null) {
            int parsed = parseNumericId(LoginController.loggedUser.getUserId());
            if (parsed > 0) {
                return parsed;
            }
        }
        return 1;
    }

    @FXML
    void clickBtnSave(ActionEvent event) {
        CalculationResult calc = computeEstimate();
        if (calc == null) {
            return;
        }

        labourTxtField.setText(String.format("%.2f", calc.labourCost));
        totalTxtField.setText(String.format("%.2f", calc.totalCost));

        Estimate est = new Estimate();
        est.setInquiryId(calc.inquiryId);
        est.setCreatedBy(resolveCreatedByUserId());
        est.setEstimateDate(LocalDateTime.now());
        est.setWidth(calc.width);
        est.setHeight(calc.height);
        est.setLength(calc.length);
        est.setQuantity(calc.qty);
        est.setWastagePct(calc.wastage);
        est.setMaterialCost(calc.materialWithWastage);
        est.setLabourCost(calc.labourCost);
        est.setOveheadCost(calc.overhead);
        est.setTotalCost(calc.totalCost);

        addEstimate(est);
    }

    private void addEstimate(Estimate est) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "INSERT INTO estimates("
                    + "inquiry_id, created_by, width, height, length, quantity, "
                    + "wastage_pct, material_cost, labour_cost, overhead_cost, total_cost) "
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, est.getInquiryId());
            pstmt.setInt(2, est.getCreatedBy());
            pstmt.setDouble(3, est.getWidth());
            pstmt.setDouble(4, est.getHeight());
            pstmt.setDouble(5, est.getLength());
            pstmt.setDouble(6, est.getQuantity());
            pstmt.setDouble(7, est.getWastagePct());
            pstmt.setDouble(8, est.getMaterialCost());
            pstmt.setDouble(9, est.getLabourCost());
            pstmt.setDouble(10, est.getOveheadCost());
            pstmt.setDouble(11, est.getTotalCost());

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Estimate saved successfully!").show();
                cleanEstimateFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    @FXML
    void clickBtnClear(ActionEvent event) {
        cleanEstimateFields();
    }

    private void cleanEstimateFields() {
        inquiryCombo.setValue(null);
        widthTxtField.setText("");
        heightTxtField.setText("");
        lengthTxtField.setText("");
        quantityTxtField.setText("");
        wastageTxtField.setText("");
        materialCostTxtField.setText("");
        overheadTxtField.setText("");
        labourTxtField.setText("");
        totalTxtField.setText("");
    }
}