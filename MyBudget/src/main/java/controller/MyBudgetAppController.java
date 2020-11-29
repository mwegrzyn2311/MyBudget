package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MyBudgetAppController {
    private Stage primaryStage;

    public MyBudgetAppController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void initRootLayout() {
        try {
            this.primaryStage.setTitle("My second JavaFX app");

            // load layout from FXML file
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MyBudgetAppController.class
                    .getResource("../view/AccountsView.fxml"));
            BorderPane rootLayout = (BorderPane) loader.load();

            // set initial data into controller
            AccountsViewController controller = loader.getController();

            // add layout to a scene and show them all
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            // don't do this in common apps
            e.printStackTrace();
        }

    }
}
