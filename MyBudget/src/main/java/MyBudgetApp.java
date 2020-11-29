import controller.MyBudgetAppController;
import javafx.application.Application;
import javafx.stage.Stage;

public class MyBudgetApp extends Application {
    private Stage primaryStage;
    private MyBudgetAppController controller;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("My first JavaFX app");

        this.controller = new MyBudgetAppController(primaryStage);
        this.controller.initRootLayout();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
