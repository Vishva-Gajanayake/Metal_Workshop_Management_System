package lk.ijse.metalworkshopmanagementsystem;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MetalWorkshopManagementSystem extends Application{
    
    private static Scene scene;
    
    @Override
    public void start(Stage stage) throws Exception {
        scene = new Scene(loadFxml("Layout"));
        stage.setScene(scene);
        stage.show();
    }
    
    public static Parent loadFxml(String fxml) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(MetalWorkshopManagementSystem.class.getResource("/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
