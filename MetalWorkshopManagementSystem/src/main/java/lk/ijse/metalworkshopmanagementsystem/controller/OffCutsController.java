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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.metalworkshopmanagementsystem.db.DBConnection;
import lk.ijse.metalworkshopmanagementsystem.model.OffCut;
import lk.ijse.metalworkshopmanagementsystem.model.OffCutStatus;
import lk.ijse.metalworkshopmanagementsystem.model.tbl.OffCutTbl;

public class OffCutsController implements Initializable {

    @FXML
    private Button clearBtn;

    @FXML
    private TableColumn<OffCutTbl, String> colId;

    @FXML
    private TableColumn<OffCutTbl, Double> colLength;

    @FXML
    private TableColumn<OffCutTbl, String> colMaterial;

    @FXML
    private TableColumn<OffCutTbl, String> colRack;

    @FXML
    private TableColumn<OffCutTbl, String> colSourceJob;

    @FXML
    private TableColumn<OffCutTbl, OffCutStatus> colStatus;

    @FXML
    private Label countLbl;

    @FXML
    private Button discardBtn;

    @FXML
    private TextField lengthTxtField;

    @FXML
    private Button markUsedBtn;

    @FXML
    private ComboBox<String> materialCombo;

    @FXML
    private TableView<OffCutTbl> offCutTbl;

    @FXML
    private AnchorPane offCutsPane;

    @FXML
    private TextField rackTxtField;

    @FXML
    private Button saveBtn;

    @FXML
    private Button searchBtn;

    @FXML
    private TextField searchTxtField;

    @FXML
    private Button showAvailableBtn;

    @FXML
    private ComboBox<String> sourceJobCombo;

    @FXML
    private ComboBox<OffCutStatus> statusCombo;

    @FXML
    private Button updateBtn;

    Connection conn;
    private boolean availableOnly = false;

    //---------------------- REGEX ----------------------
    private static final String RACK_REGEX = "^[A-Za-z0-9 \\-]{1,20}$";
    private static final String DIMENSION_REGEX = "^\\d{1,3}(\\.\\d{1,2})?$";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("offCutId"));
        colMaterial.setCellValueFactory(new PropertyValueFactory<>("materialId"));
        colLength.setCellValueFactory(new PropertyValueFactory<>("length"));
        colRack.setCellValueFactory(new PropertyValueFactory<>("rackLocation"));
        colSourceJob.setCellValueFactory(new PropertyValueFactory<>("sourceJobId"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        offCutTbl.setOnMouseClicked(e -> {
            OffCutTbl row = offCutTbl.getSelectionModel().getSelectedItem();
            if (row != null) {
                searchTxtField.setText(row.getOffCutId());
                fillFields(parseNumericId(row.getOffCutId()));
            }
        });

        loadMaterialComboBox();
        loadSourceJobComboBox();
        loadOffCutStatusCombo();

        loadOffCutTable();
    }

    private int parseNumericId(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
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

    //---------------------- LOAD COMBO BOXES ----------------------
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
                materialList.add(matCode);
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

    private void loadSourceJobComboBox() {
        List<String> jobList = new ArrayList<>();
        ObservableList<String> jobObList = FXCollections.observableArrayList();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM job_orders ORDER BY job_id ASC";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String jobName = result.getString("job_no");
                jobList.add(jobName);
            }

            jobObList.addAll(jobList);
            sourceJobCombo.setItems(jobObList);

            result.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    private void loadOffCutStatusCombo() {
        statusCombo.setItems(FXCollections.observableArrayList(OffCutStatus.values()));
    }

    //---------------------- SAVE OFF-CUTS ----------------------
    @FXML
    void clickBtnSave(ActionEvent event) {
        String materialCode = materialCombo.getSelectionModel().getSelectedItem();
        String lengthText = lengthTxtField.getText().trim();
        String rackLocation = rackTxtField.getText().trim();
        String sourceJob = sourceJobCombo.getSelectionModel().getSelectedItem();
        OffCutStatus status = statusCombo.getSelectionModel().getSelectedItem();

        if (materialCode == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a material").show();
            return;
        }
        if (lengthText.isEmpty() || !lengthText.matches(DIMENSION_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid length").show();
            return;
        }
        double length = Double.parseDouble(lengthText);
        if (length <= 0) {
            new Alert(Alert.AlertType.ERROR, "Length must be greater than 0").show();
            return;
        }
        if (!rackLocation.matches(RACK_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid rack location").show();
            return;
        }
        if (sourceJob == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a source job").show();
            return;
        }
        if (status == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a status").show();
            return;
        }

        OffCut offCutModel = new OffCut();
        offCutModel.setMaterialId(getMaterialIdByName(materialCode));
        offCutModel.setSourceJobId(getJobId(sourceJob));
        offCutModel.setLength(length);
        offCutModel.setRackLocation(rackLocation);
        offCutModel.setStatus(status);
        offCutModel.setRecordedDate(LocalDate.now());

        addOffCut(offCutModel);
    }

    private void addOffCut(OffCut offCutModel) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String queryInsert = "INSERT INTO off_cuts("
                    + "material_id, "
                    + "source_job_id, "
                    + "length, "
                    + "rack_location, "
                    + "status, "
                    + "recorded_date) "
                    + "VALUES(?,?,?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(queryInsert);
            pstmt.setInt(1, offCutModel.getMaterialId());
            pstmt.setInt(2, offCutModel.getSourceJobId());
            pstmt.setDouble(3, offCutModel.getLength());
            pstmt.setString(4, offCutModel.getRackLocation());
            pstmt.setString(5, offCutModel.getStatus().name());
            pstmt.setObject(6, offCutModel.getRecordedDate());

            int result1 = pstmt.executeUpdate();
            pstmt.close();

            if (result1 > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Off-Cut saved successfully!").show();
                loadOffCutTable();
                cleanOffCutFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    private int getMaterialIdByName(String materialCode) {
        int materialId = 0;
        if (materialCode == null) {
            return materialId;
        }
        String code = materialCode.trim().split("\\s+")[0];

        try {
            conn = DBConnection.getInstance().getConnection();
            String query = "SELECT material_id FROM materials WHERE material_code=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, code);

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

    private int getJobId(String jobNum) {
        int jobId = 0;
        if (jobNum == null || jobNum.trim().isEmpty()) {
            return jobId;
        }

        try {
            conn = DBConnection.getInstance().getConnection();
            String query = "SELECT job_id FROM job_orders WHERE job_no=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, jobNum.trim());

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                jobId = result.getInt("job_id");
            }
            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return jobId;
    }

    //---------------------- LOAD OFF-CUT TABLE ----------------------
    private void loadOffCutTable() {
        List<OffCut> offCutList = getAllOffCuts();
        List<OffCutTbl> offCutTblList = new ArrayList<>();

        for (OffCut offCut : offCutList) {
            if (availableOnly && offCut.getStatus() != OffCutStatus.AVAILABLE) {
                continue;
            }

            String matLabel = getMaterialById(offCut.getMaterialId());
            String jobLabel = getJobById(offCut.getSourceJobId());

            OffCutTbl offCutTblRow = new OffCutTbl(
                    String.format("OC%03d", offCut.getOffCutId()),
                    matLabel != null ? matLabel : String.format("M%03d", offCut.getMaterialId()),
                    jobLabel != null ? jobLabel : String.format("J%03d", offCut.getSourceJobId()),
                    offCut.getLength(),
                    offCut.getRackLocation(),
                    offCut.getStatus()
            );
            offCutTblList.add(offCutTblRow);
        }

        ObservableList<OffCutTbl> obList
                = FXCollections.observableArrayList(offCutTblList);

        offCutTbl.setItems(obList);
        if (countLbl != null) {
            countLbl.setText("Showing " + offCutTblList.size() + " of " + offCutList.size() + " records");
        }
    }

    private List<OffCut> getAllOffCuts() {
        List<OffCut> offCutList = new ArrayList<>();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM off_cuts ORDER BY offcut_id DESC";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                OffCut offCut = new OffCut();
                offCut.setOffCutId(result.getInt("offcut_id"));
                offCut.setMaterialId(result.getInt("material_id"));
                offCut.setSourceJobId(result.getInt("source_job_id"));
                offCut.setLength(result.getDouble("length"));
                offCut.setRackLocation(result.getString("rack_location"));
                offCut.setStatus(toStatus(result.getString("status")));

                String date = result.getString("recorded_date");
                if (date != null && date.length() >= 10) {
                    offCut.setRecordedDate(LocalDate.parse(date.substring(0, 10)));
                }

                offCutList.add(offCut);
            }

            result.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return offCutList;
    }

    private OffCutStatus toStatus(String text) {
        try {
            return OffCutStatus.valueOf(text);
        } catch (IllegalArgumentException | NullPointerException e) {
            return OffCutStatus.AVAILABLE;
        }
    }

    //---------------------- SHOW AVAILABLE OFF-CUTS ----------------------
    @FXML
    void clickBtnShowAvailable(ActionEvent event) {
        availableOnly = !availableOnly;
        if (showAvailableBtn != null) {
            showAvailableBtn.setText(availableOnly ? "Show All" : "Available Only");
        }
        loadOffCutTable();
    }

    //---------------------- UPDATE OFF-CUTS ----------------------
    @FXML
    void clickBtnUpdate(ActionEvent event) {
        int id = parseNumericId(searchTxtField.getText());
        String materialCode = materialCombo.getSelectionModel().getSelectedItem();
        String lengthText = lengthTxtField.getText().trim();
        String rackLocation = rackTxtField.getText().trim();
        String sourceJob = sourceJobCombo.getSelectionModel().getSelectedItem();
        OffCutStatus status = statusCombo.getSelectionModel().getSelectedItem();

        if (id <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please search or select an Off-Cut first (e.g., OC001)").show();
            return;
        }
        if (materialCode == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a material").show();
            return;
        }
        if (lengthText.isEmpty() || !lengthText.matches(DIMENSION_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid length").show();
            return;
        }
        double length = Double.parseDouble(lengthText);
        if (length <= 0) {
            new Alert(Alert.AlertType.ERROR, "Length must be greater than 0").show();
            return;
        }
        if (!rackLocation.matches(RACK_REGEX)) {
            new Alert(Alert.AlertType.ERROR, "Invalid rack location").show();
            return;
        }
        if (sourceJob == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a source job").show();
            return;
        }
        if (status == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a status").show();
            return;
        }

        OffCut offCutModel = new OffCut();
        offCutModel.setOffCutId(id);
        offCutModel.setMaterialId(getMaterialIdByName(materialCode));
        offCutModel.setSourceJobId(getJobId(sourceJob));
        offCutModel.setLength(length);
        offCutModel.setRackLocation(rackLocation);
        offCutModel.setStatus(status);
        offCutModel.setRecordedDate(LocalDate.now());

        updateOffCut(offCutModel);
    }

    private void updateOffCut(OffCut offCutModel) {
        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "UPDATE off_cuts SET "
                    + "material_id=?, "
                    + "source_job_id=?, "
                    + "length=?, "
                    + "rack_location=?, "
                    + "status=? "
                    + "WHERE offcut_id=?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, offCutModel.getMaterialId());
            pstmt.setInt(2, offCutModel.getSourceJobId());
            pstmt.setDouble(3, offCutModel.getLength());
            pstmt.setString(4, offCutModel.getRackLocation());
            pstmt.setString(5, offCutModel.getStatus().name());
            pstmt.setInt(6, offCutModel.getOffCutId());

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Off-Cut updated successfully!").show();
                loadOffCutTable();
                cleanOffCutFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Off-Cut not found!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    @FXML
    void clickBtnMarkUsed(ActionEvent event) {
        updateStatusOnly(OffCutStatus.USED);
    }

    @FXML
    void clickBtnDiscard(ActionEvent event) {
        updateStatusOnly(OffCutStatus.DISCARD);
    }

    private void updateStatusOnly(OffCutStatus newStatus) {
        int id = parseNumericId(searchTxtField.getText());
        if (id <= 0) {
            OffCutTbl selected = offCutTbl.getSelectionModel().getSelectedItem();
            if (selected != null) {
                id = parseNumericId(selected.getOffCutId());
            }
        }
        if (id <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please select or search an Off-Cut first").show();
            return;
        }

        try {
            conn = DBConnection.getInstance().getConnection();
            String query = "UPDATE off_cuts SET status=? WHERE offcut_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, newStatus.name());
            pstmt.setInt(2, id);

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Off-Cut marked as " + newStatus.getLabel() + "!").show();
                loadOffCutTable();
                cleanOffCutFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Off-Cut not found!").show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    //---------------------- SEARCH OFF-CUTS ----------------------
    @FXML
    void clickBtnSearch(ActionEvent event) {
        fillFields(parseNumericId(searchTxtField.getText()));
    }

    @FXML
    void pressSearchOffCut(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            fillFields(parseNumericId(searchTxtField.getText()));
        }
    }

    private void fillFields(int offCutId) {
        if (offCutId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid Off-Cut ID (e.g., OC001)").show();
            return;
        }

        OffCut offCutModel = searchOffCut(offCutId);

        if (offCutModel != null) {
            String matCode = getMaterialCodeOnlyById(offCutModel.getMaterialId());
            if (matCode != null) {
                materialCombo.setValue(matCode);
            }
            lengthTxtField.setText(String.valueOf(offCutModel.getLength()));
            rackTxtField.setText(offCutModel.getRackLocation());
            String jobNo = getJobById(offCutModel.getSourceJobId());
            if (jobNo != null) {
                sourceJobCombo.setValue(jobNo);
            }
            statusCombo.setValue(offCutModel.getStatus());
        }
    }

    private OffCut searchOffCut(int offCutId) {
        OffCut offCutModel = null;

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * FROM off_cuts WHERE offcut_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, offCutId);

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                offCutModel = new OffCut();
                offCutModel.setOffCutId(offCutId);
                offCutModel.setMaterialId(result.getInt("material_id"));
                offCutModel.setLength(result.getDouble("length"));
                offCutModel.setRackLocation(result.getString("rack_location"));
                offCutModel.setSourceJobId(result.getInt("source_job_id"));
                offCutModel.setStatus(toStatus(result.getString("status")));
            } else {
                new Alert(Alert.AlertType.ERROR, "Off-Cut not found!").show();
            }

            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return offCutModel;
    }

    private String getMaterialCodeOnlyById(int materialId) {
        String code = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            String query = "SELECT material_code FROM materials WHERE material_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, materialId);

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                code = result.getString("material_code");
            }
            result.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return code;
    }

    private String getMaterialById(int materialId) {
        String material = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            String query = "SELECT material_code, material_name FROM materials WHERE material_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, materialId);

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                String code = result.getString("material_code");
                String name = result.getString("material_name");
                material = code + "  " + name;
            }
            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return material;
    }

    private String getJobById(int jobId) {
        String job = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            String query = "SELECT job_no FROM job_orders WHERE job_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, jobId);

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                job = result.getString("job_no");
            }
            result.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return job;
    }

    //---------------------- CLEAN FIELDS ----------------------
    @FXML
    void clickBtnClear(ActionEvent event) {
        cleanOffCutFields();
    }

    private void cleanOffCutFields() {
        searchTxtField.setText("");
        materialCombo.setValue(null);
        lengthTxtField.setText("");
        rackTxtField.setText("");
        sourceJobCombo.setValue(null);
        statusCombo.setValue(null);
        offCutTbl.getSelectionModel().clearSelection();
    }
}