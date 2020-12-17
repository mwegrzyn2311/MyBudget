import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import dao.CategoryDao;
import dao.TopCategoryDao;
import guice_module.MyBudgetModule;
import helper.DBInitHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import org.controlsfx.tools.ValueExtractor;
import service.FxmlLoaderService;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.IOException;

public class MyBudgetApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Hack to fix controlsfx library
        ValueExtractor.addObservableValueExtractor(con -> con instanceof TreeView, con -> ((TreeView<?>)con).getSelectionModel().selectedItemProperty());

        final File db = new File("dby.db");
        final boolean dbAlreadyExisted = db.exists();

        final Injector injector = Guice.createInjector(new MyBudgetModule());
        injector.getInstance(PersistService.class).start();
        if(!dbAlreadyExisted) {
            DBInitHelper.initializeDB(injector.getInstance(TopCategoryDao.class), injector.getInstance(CategoryDao.class));
        }

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
