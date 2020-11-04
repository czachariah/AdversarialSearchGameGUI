package Visualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This is the main class used in order to run the GUI for the Adversarial Search Game.
 */
public class Main extends Application {

    // fixed parameters for the GUI size
    public final int FIXED_WIDTH = 700;
    public final int FIXED_LENGTH = 1200;

    /**
     * This method is used in order to set up the main stage for the GUI.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/Visualizer/BoardViewer.fxml"));
        primaryStage.setTitle("Wumpus World Chess");
        primaryStage.setScene(new Scene(root, FIXED_LENGTH, FIXED_WIDTH));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
