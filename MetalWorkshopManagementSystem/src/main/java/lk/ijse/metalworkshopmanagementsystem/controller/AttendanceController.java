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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.metalworkshopmanagementsystem.db.DBConnection;
import lk.ijse.metalworkshopmanagementsystem.model.Attendance;
import lk.ijse.metalworkshopmanagementsystem.model.AttendanceStatus;
import lk.ijse.metalworkshopmanagementsystem.model.tbl.AttendanceTbl;
import lk.ijse.metalworkshopmanagementsystem.model.tbl.AttendanceWorkerTbl;

public class AttendanceController implements Initializable {

    Connection conn;

    @FXML
    private TableView<AttendanceTbl> AttendanceTbl;

    @FXML
    private AnchorPane attendancePane;

    @FXML
    private TableColumn<AttendanceTbl, String> colAttendOvertime;

    @FXML
    private TableColumn<AttendanceTbl, String> colAttendStatus;

    @FXML
    private TableColumn<AttendanceTbl, String> colAttendWorker;

    @FXML
    private TableColumn<AttendanceWorkerTbl, String> colDate;

    @FXML
    private TableColumn<AttendanceWorkerTbl, Void> colOvertime;

    @FXML
    private TableColumn<AttendanceWorkerTbl, String> colStatus;

    @FXML
    private TableColumn<AttendanceWorkerTbl, String> colWorker;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button loadBtn;

    @FXML
    private Button markAbsentBtn;

    @FXML
    private Button markHalfdayBtn;

    @FXML
    private Button markPresentBtn;

    @FXML
    private Button markSaveBtn;

    @FXML
    private TableView<AttendanceWorkerTbl> workerTbl;

    AttendanceWorkerTbl selectedWorker = null;

    List<AttendanceWorkerTbl> workerList = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colWorker.setCellValueFactory(new PropertyValueFactory<>("worker"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colAttendWorker.setCellValueFactory(new PropertyValueFactory<>("worker"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("attendanceDate"));
        colAttendStatus.setCellValueFactory(new PropertyValueFactory<>("attendanceStatus"));
        colAttendOvertime.setCellValueFactory(new PropertyValueFactory<>("overtime"));

        datePicker.setValue(LocalDate.now());

        //---------------------- ADD OVERTIME INPUT FIELD ----------------------
        colOvertime.setCellFactory(col -> new TableCell<AttendanceWorkerTbl, Void>() {

            private final TextField txtOvertime = new TextField();

            {
                txtOvertime.setOnKeyReleased(event -> {
                    int rowIndex = getIndex();
                    if (rowIndex < 0 || rowIndex >= getTableView().getItems().size()) {
                        return;
                    }

                    AttendanceWorkerTbl worker = getTableView().getItems().get(rowIndex);
                    String value = txtOvertime.getText().trim();

                    if (value.isEmpty()) {
                        worker.setOvertime(0.0);
                    } else {
                        try {
                            double ot = Double.parseDouble(value);
                            if (ot >= 0) {
                                worker.setOvertime(ot);
                            }
                        } catch (NumberFormatException e) {
                            // Keep previous valid overtime value
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    AttendanceWorkerTbl worker = getTableView().getItems().get(getIndex());
                    txtOvertime.setText(String.valueOf(worker.getOvertime()));
                    setGraphic(txtOvertime);
                }
            }
        });

        loadAttendanceHistory();
    }

    //---------------------- LOAD WORKER TABLE ----------------------

    @FXML
    void clickBtnLoad(ActionEvent event) {
        loadWorkerTable();
    }

    private void loadWorkerTable() {
        workerList = getAllWorkers();

        ObservableList<AttendanceWorkerTbl> obList
                = FXCollections.observableArrayList(workerList);

        workerTbl.setItems(obList);
    }

    private List<AttendanceWorkerTbl> getAllWorkers() {
        List<AttendanceWorkerTbl> list = new ArrayList<>();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT * "
                    + "FROM workers "
                    + "WHERE is_active = '1' "
                    + "ORDER BY worker_id DESC";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String id = String.format("W%03d", result.getInt("worker_id"));
                String name = result.getString("full_name");

                AttendanceWorkerTbl worker = new AttendanceWorkerTbl();
                worker.setWorker(id + " " + name);

                list.add(worker);
            }

            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return list;
    }

    private void loadAttendanceHistory() {
        List<AttendanceTbl> attendanceTblList = new ArrayList<>();

        try {
            conn = DBConnection.getInstance().getConnection();

            String query = "SELECT "
                    + "a.attendance_date, "
                    + "w.full_name, "
                    + "a.status, "
                    + "a.overtime_hours "
                    + "FROM attendance a "
                    + "INNER JOIN workers w "
                    + "ON a.worker_id = w.worker_id "
                    + "ORDER BY a.attendance_date DESC";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                AttendanceTbl attTbl = new AttendanceTbl(
                        LocalDate.parse(result.getString("attendance_date")),
                        result.getString("full_name"),
                        toStatus(result.getString("status")),
                        Double.parseDouble(result.getString("overtime_hours"))
                );

                attendanceTblList.add(attTbl);
            }

            stmt.close();

            ObservableList<AttendanceTbl> obList
                    = FXCollections.observableArrayList(attendanceTblList);

            AttendanceTbl.setItems(obList);

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load attendance history!").show();
        }
    }

    private AttendanceStatus toStatus(String text) {
        try {
            return AttendanceStatus.valueOf(text);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    private int extractWorkerId(String workerLabel) {
        if (workerLabel == null) {
            return -1;
        }
        String token = workerLabel.trim().split("\\s+")[0];
        String digits = token.replaceAll("\\D+", "");
        if (digits.isEmpty()) {
            return -1;
        }
        return Integer.parseInt(digits);
    }

    @FXML
    void clickGetWorkerRecord(MouseEvent event) {
        selectedWorker = workerTbl.getSelectionModel().getSelectedItem();
    }

    @FXML
    void clickBrnPresent(ActionEvent event) {
        selectedWorker = workerTbl.getSelectionModel().getSelectedItem();

        if (selectedWorker == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a worker first!").show();
            return;
        }

        selectedWorker.setStatus(AttendanceStatus.PRESENT);
        workerTbl.refresh();
    }

    @FXML
    void clickBtnAbsent(ActionEvent event) {
        selectedWorker = workerTbl.getSelectionModel().getSelectedItem();

        if (selectedWorker == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a worker first!").show();
            return;
        }

        selectedWorker.setStatus(AttendanceStatus.ABSENT);
        workerTbl.refresh();
    }

    @FXML
    void clickBtnHalfDay(ActionEvent event) {
        selectedWorker = workerTbl.getSelectionModel().getSelectedItem();

        if (selectedWorker == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a worker first!").show();
            return;
        }

        selectedWorker.setStatus(AttendanceStatus.HALF_DAY);
        workerTbl.refresh();
    }

    @FXML
    void clickBtnSave(ActionEvent event) {
        if (datePicker.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Please select an attendance date!").show();
            return;
        }

        if (workerList == null || workerList.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please load workers first!").show();
            return;
        }

        for (AttendanceWorkerTbl row : workerList) {
            if (row.getStatus() == null) {
                new Alert(Alert.AlertType.WARNING,
                        "Please set status (Present/Absent/Half Day) for " + row.getWorker()).show();
                return;
            }
        }

        int savedCount = 0;

        for (AttendanceWorkerTbl attendanceWorkerTbl : workerList) {
            int workerId = extractWorkerId(attendanceWorkerTbl.getWorker());
            if (workerId <= 0) {
                new Alert(Alert.AlertType.ERROR, "Invalid worker ID for " + attendanceWorkerTbl.getWorker()).show();
                return;
            }

            Attendance attModel = new Attendance();
            attModel.setAttendanceDate(datePicker.getValue());
            attModel.setWorkerId(workerId);
            attModel.setAttendanceStatus(attendanceWorkerTbl.getStatus());
            attModel.setOvertime(attendanceWorkerTbl.getOvertime());

            if (saveAttendance(attModel) == 1) {
                savedCount++;
            } else {
                new Alert(Alert.AlertType.ERROR, "Something went wrong while saving attendance!").show();
                return;
            }
        }

        if (savedCount == workerList.size()) {
            loadAttendanceHistory();
            new Alert(Alert.AlertType.INFORMATION, "Attendance records saved successfully!").show();
        }
    }

    private int saveAttendance(Attendance attModel) {
        int i = 0;

        try {
            conn = DBConnection.getInstance().getConnection();

            String queryInsert = "INSERT INTO attendance("
                    + "worker_id, "
                    + "attendance_date, "
                    + "status, "
                    + "overtime_hours) "
                    + "VALUES(?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(queryInsert);

            pstmt.setInt(1, attModel.getWorkerId());
            pstmt.setObject(2, attModel.getAttendanceDate());
            pstmt.setString(3, attModel.getAttendanceStatus().name());
            pstmt.setDouble(4, attModel.getOvertime());

            int result1 = pstmt.executeUpdate();
            pstmt.close();

            if (result1 > 0) {
                i = 1;
            } else {
                i = -1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }

        return i;
    }
}