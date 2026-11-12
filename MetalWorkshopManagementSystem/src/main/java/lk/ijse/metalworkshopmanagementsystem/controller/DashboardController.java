package lk.ijse.metalworkshopmanagementsystem.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class DashboardController implements Initializable{
    
    @FXML
    private Text activeJobsLbl;

    @FXML
    private AnchorPane dashboardPane;

    @FXML
    private TableView<?> dueJobsTbl;

    @FXML
    private Text endingQuotationsLbl;

    @FXML
    private PieChart jobsByCategoryChart;

    @FXML
    private TableView<?> lowStockAlertsTbl;

    @FXML
    private Text lowStockItemsLbl;

    @FXML
    private Button newEstimateBtn;

    @FXML
    private Button openReportBtn;

    @FXML
    private Text outstandingBalanceLbl;

    @FXML
    private StackedBarChart<?, ?> revenueVsCostChart;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    
    
}
