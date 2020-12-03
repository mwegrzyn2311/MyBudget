import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import guice_module.MyBudgetModule;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.FxmlLoaderService;

import java.io.IOException;

public class MyBudgetApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        final Injector injector = Guice.createInjector(new MyBudgetModule());
        injector.getInstance(PersistService.class).start();
        final FxmlLoaderService fxmlLoaderService = injector.getInstance(FxmlLoaderService.class);
        try {
            Parent root = fxmlLoaderService.getLoader(getClass().getResource("/view/MyBudgetApp.fxml")).load();
            Scene scene = new Scene(root, 800, 600);


            primaryStage.setScene(scene);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
