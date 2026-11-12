package lk.ijse.metalworkshopmanagementsystem.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import lk.ijse.metalworkshopmanagementsystem.MetalWorkshopManagementSystem;

public class LayoutController implements Initializable{
    
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
    void navBtnDashboard(ActionEvent event) throws IOException{
        Parent dashboardFxml = MetalWorkshopManagementSystem.loadFxml("Dashboard");
        getAnchorAlignment(dashboardFxml);
        mainPane.getChildren().setAll(dashboardFxml);
    }
    
    @FXML
    void navBtnClients(ActionEvent event) throws IOException {
        Parent clientsFxml = MetalWorkshopManagementSystem.loadFxml("Clients");
        getAnchorAlignment(clientsFxml);
        mainPane.getChildren().setAll(clientsFxml);
    }
    
    @FXML
    void navBtnInquiries(ActionEvent event) throws IOException {
        Parent inquiriesFxml = MetalWorkshopManagementSystem.loadFxml("Inquiries");
        getAnchorAlignment(inquiriesFxml);
        mainPane.getChildren().setAll(inquiriesFxml);
    }
    
    @FXML
    void navBtnTasks(ActionEvent event) throws IOException {
        Parent tasksFxml = MetalWorkshopManagementSystem.loadFxml("Tasks");
        getAnchorAlignment(tasksFxml);
        mainPane.getChildren().setAll(tasksFxml);
    }
    
    @FXML
    void navBtnWorkers(ActionEvent event) throws IOException {
        Parent workersFxml = MetalWorkshopManagementSystem.loadFxml("Workers");
        getAnchorAlignment(workersFxml);
        mainPane.getChildren().setAll(workersFxml);
    }
    
    @FXML
    void navBtnServiceCategories(ActionEvent event) throws IOException {
        Parent serviceCategoriesFxml = MetalWorkshopManagementSystem.loadFxml("ServiceCategories");
        getAnchorAlignment(serviceCategoriesFxml);
        mainPane.getChildren().setAll(serviceCategoriesFxml);
    }
    
    @FXML
    void navBtnSuppliers(ActionEvent event) throws IOException {
        Parent suppliersFxml = MetalWorkshopManagementSystem.loadFxml("Suppliers");
        getAnchorAlignment(suppliersFxml);
        mainPane.getChildren().setAll(suppliersFxml);
    }
    
    @FXML
    void navBtnWageAdvances(ActionEvent event) throws IOException {
        Parent wagesAdvancesFxml = MetalWorkshopManagementSystem.loadFxml("WagesAdvances");
        getAnchorAlignment(wagesAdvancesFxml);
        mainPane.getChildren().setAll(wagesAdvancesFxml);
    }
    
    @FXML
    void navBtnEstimator(ActionEvent event) throws IOException {
        Parent estimatorAdvancesFxml = MetalWorkshopManagementSystem.loadFxml("Estimator");
        getAnchorAlignment(estimatorAdvancesFxml);
        mainPane.getChildren().setAll(estimatorAdvancesFxml);
    }
    
    @FXML
    void navBtnQuotations(ActionEvent event) throws IOException {
        Parent quotationsFxml = MetalWorkshopManagementSystem.loadFxml("Quotations");
        getAnchorAlignment(quotationsFxml);
        mainPane.getChildren().setAll(quotationsFxml);
    }
    
    @FXML
    void navBtnMaterialStock(ActionEvent event) throws IOException {
        Parent materialStockFxml = MetalWorkshopManagementSystem.loadFxml("MaterialStock");
        getAnchorAlignment(materialStockFxml);
        mainPane.getChildren().setAll(materialStockFxml);
    }
    
    private void getAnchorAlignment(Parent fxml) {
        AnchorPane.setTopAnchor(fxml, 0.0);
        AnchorPane.setBottomAnchor(fxml, 0.0);
        AnchorPane.setLeftAnchor(fxml, 0.0);
        AnchorPane.setRightAnchor(fxml, 0.0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
}