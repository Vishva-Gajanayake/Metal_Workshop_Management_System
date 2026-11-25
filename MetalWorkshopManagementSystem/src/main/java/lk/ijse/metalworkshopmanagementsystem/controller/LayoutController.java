package lk.ijse.metalworkshopmanagementsystem.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import lk.ijse.metalworkshopmanagementsystem.MetalWorkshopManagementSystem;
import lk.ijse.metalworkshopmanagementsystem.model.Role;
import lk.ijse.metalworkshopmanagementsystem.model.User;

public class LayoutController implements Initializable {

    @FXML
    private Button EstimatorBtn;

    @FXML
    private Button attendanceBtn;

    @FXML
    private Button backupRestoreBtn;

    @FXML
    private Button clientsBtn;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button goodsReceivedBtn;

    @FXML
    private Button inquiriesBtn;

    @FXML
    private Button invoicesBtn;

    @FXML
    private Button jobOrdersBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Button materialStockBtn;

    @FXML
    private Button offCutsBtn;

    @FXML
    private Button paymentsBtn;

    @FXML
    private Button quotationsBtn;

    @FXML
    private Button reportsBtn;

    @FXML
    private Button serviceCategoriesBtn;

    @FXML
    private Button suppliersBtn;

    @FXML
    private Button tasksBtn;

    @FXML
    private Button usersBtn;

    @FXML
    private Button wageAdvancesBtn;

    @FXML
    private Button wagesBtn;

    @FXML
    private Button workersBtn;

    @FXML
    private Text headerTxt;

    @FXML
    private Label userInitialLbl;

    @FXML
    private Label loggedUserLbl;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        displayLoggedInUser();
        applyRolePermissions();
        if (getClass().getResource("/view/Dashboard.fxml") != null) {
            loadView("Dashboard", "Dashboard", dashboardBtn);
        } else if (headerTxt != null) {
            headerTxt.setText("Dashboard");
        }
    }

    private void displayLoggedInUser() {
        User currentUser = LoginController.loggedUser;
        String displayName = "Owner";

        if (currentUser != null) {
            if (currentUser.getFullName() != null && !currentUser.getFullName().trim().isEmpty()) {
                displayName = currentUser.getFullName().trim();
            } else if (currentUser.getUsername() != null && !currentUser.getUsername().trim().isEmpty()) {
                displayName = currentUser.getUsername().trim();
            }
        }

        if (loggedUserLbl != null) {
            loggedUserLbl.setText(displayName);
        }
        if (userInitialLbl != null) {
            userInitialLbl.setText(displayName.substring(0, 1).toUpperCase());
        }
    }

    private void applyRolePermissions() {
        User currentUser = LoginController.loggedUser;
        Role role = (currentUser != null && currentUser.getRole() != null)
                ? currentUser.getRole()
                : Role.OWNER;

        setAllNavButtonsVisible(true);

        if (role == Role.OWNER) {
            return;
        }

        if (role == Role.ASSISTANT) {
            setButtonVisible(usersBtn, false);
            setButtonVisible(backupRestoreBtn, false);
            setButtonVisible(wagesBtn, false);
        } else {
            // MASTER_FABRICATOR, WELDER, PAINTER, WORKSHOP_HELPER
            setButtonVisible(usersBtn, false);
            setButtonVisible(backupRestoreBtn, false);
            setButtonVisible(wagesBtn, false);
            setButtonVisible(wageAdvancesBtn, false);
            setButtonVisible(paymentsBtn, false);
            setButtonVisible(invoicesBtn, false);
            setButtonVisible(suppliersBtn, false);
            setButtonVisible(goodsReceivedBtn, false);
        }
    }

    private void setAllNavButtonsVisible(boolean visible) {
        Button[] allButtons = {
            dashboardBtn, clientsBtn, inquiriesBtn, EstimatorBtn, quotationsBtn,
            jobOrdersBtn, tasksBtn, materialStockBtn, goodsReceivedBtn, offCutsBtn,
            suppliersBtn, serviceCategoriesBtn, workersBtn, attendanceBtn,
            wageAdvancesBtn, wagesBtn, paymentsBtn, invoicesBtn, reportsBtn,
            usersBtn, backupRestoreBtn
        };

        for (Button btn : allButtons) {
            setButtonVisible(btn, visible);
        }
    }

    private void setButtonVisible(Button button, boolean visible) {
        if (button != null) {
            button.setVisible(visible);
            button.setManaged(visible);
        }
    }

    private void highlightActiveNavButton(Button activeBtn) {
        Button[] allButtons = {
            dashboardBtn, clientsBtn, inquiriesBtn, EstimatorBtn, quotationsBtn,
            jobOrdersBtn, tasksBtn, materialStockBtn, goodsReceivedBtn, offCutsBtn,
            suppliersBtn, serviceCategoriesBtn, workersBtn, attendanceBtn,
            wageAdvancesBtn, wagesBtn, paymentsBtn, invoicesBtn, reportsBtn,
            usersBtn, backupRestoreBtn
        };

        for (Button btn : allButtons) {
            if (btn != null) {
                btn.getStyleClass().remove("navBtn-active");
            }
        }
        if (activeBtn != null && !activeBtn.getStyleClass().contains("navBtn-active")) {
            activeBtn.getStyleClass().add("navBtn-active");
        }
    }

    private void loadView(String fxmlName, String title, Button activeBtn) {
        try {
            Parent view = MetalWorkshopManagementSystem.loadFxml(fxmlName);
            getAnchorAlignment(view);
            mainPane.getChildren().setAll(view);
            if (headerTxt != null) {
                headerTxt.setText(title);
            }
            highlightActiveNavButton(activeBtn);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load " + title + " screen!").show();
        }
    }

    @FXML
    void navBtnDashboard(ActionEvent event) throws IOException {
        loadView("Dashboard", "Dashboard", dashboardBtn);
    }

    @FXML
    void navBtnClients(ActionEvent event) throws IOException {
        loadView("Clients", "Client Management", clientsBtn);
    }

    @FXML
    void navBtnInquiries(ActionEvent event) throws IOException {
        loadView("Inquiries", "Inquiry Management", inquiriesBtn);
    }

    @FXML
    void navBtnTasks(ActionEvent event) throws IOException {
        loadView("Tasks", "Task Management", tasksBtn);
    }

    @FXML
    void navBtnWorkers(ActionEvent event) throws IOException {
        loadView("Workers", "Worker Management", workersBtn);
    }

    @FXML
    void navBtnServiceCategories(ActionEvent event) throws IOException {
        loadView("ServiceCategories", "Service Category Management", serviceCategoriesBtn);
    }

    @FXML
    void navBtnSuppliers(ActionEvent event) throws IOException {
        loadView("Suppliers", "Supplier Management", suppliersBtn);
    }

    @FXML
    void navBtnWageAdvances(ActionEvent event) throws IOException {
        loadView("WageAdvances", "Wage Advance Management", wageAdvancesBtn);
    }

    @FXML
    void navBtnEstimator(ActionEvent event) throws IOException {
        loadView("Estimator", "Estimator Management", EstimatorBtn);
    }

    @FXML
    void navBtnQuotations(ActionEvent event) throws IOException {
        loadView("Quotations", "Quotation Management", quotationsBtn);
    }

    @FXML
    void navBtnMaterialStock(ActionEvent event) throws IOException {
        loadView("MaterialStock", "Material Stock Management", materialStockBtn);
    }

    @FXML
    void navBtnUsers(ActionEvent event) throws IOException {
        loadView("Users", "User Management", usersBtn);
    }

    @FXML
    void navBtnGoodsReceived(ActionEvent event) throws IOException {
        loadView("GoodsReceived", "Goods Received Management", goodsReceivedBtn);
    }

    @FXML
    void navBtnOffCut(ActionEvent event) throws IOException {
        loadView("OffCuts", "Off-Cuts Management", offCutsBtn);
    }

    @FXML
    void navBtnAttendance(ActionEvent event) throws IOException {
        loadView("Attendance", "Attendance Management", attendanceBtn);
    }

    @FXML
    void navBtnJobOrders(ActionEvent event) throws IOException {
        loadView("JobOrders", "Job Orders Management", jobOrdersBtn);
    }

    @FXML
    void vanBtnJobOrders(ActionEvent event) throws IOException {
        navBtnJobOrders(event);
    }

    @FXML
    void navBtnClientPayments(ActionEvent event) {
        loadView("ClientPayments", "Client Payments Management", paymentsBtn);
    }

    @FXML
    void clickLogoutBtn(ActionEvent event) throws IOException {
        LoginController.loggedUser = null;
        MetalWorkshopManagementSystem.getScene().setRoot(MetalWorkshopManagementSystem.loadFxml("Login"));
    }

    private void getAnchorAlignment(Parent fxml) {
        AnchorPane.setTopAnchor(fxml, 0.0);
        AnchorPane.setBottomAnchor(fxml, 0.0);
        AnchorPane.setLeftAnchor(fxml, 0.0);
        AnchorPane.setRightAnchor(fxml, 0.0);
    }

    @FXML
    void clickUserBtn(ActionEvent event) {
        User currentUser = LoginController.loggedUser;
        String name = (currentUser != null && currentUser.getFullName() != null)
                ? currentUser.getFullName()
                : "Owner";
        String role = (currentUser != null && currentUser.getRole() != null)
                ? currentUser.getRole().name()
                : "OWNER";
        new Alert(Alert.AlertType.INFORMATION, "Logged in as: " + name + " (" + role + ")").show();
    }
}