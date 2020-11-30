package service;

import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FxmlLoaderService {
    private final Injector injector;

    @Inject
    public FxmlLoaderService(final Injector injector) {
        this.injector = injector;
    }

    /**
     * Create controllers using Guice to inject dependencies
     * @param location location URI
     * @return Node
     */
    public Parent load(final URL location) throws IOException {
        return load(location, null);
    }

    /**
     * Create controllers using Guice to inject dependencies
     * @param location location URI
     * @param resources resources bundle
     * @return Node
     */
    public Parent load(final URL location, ResourceBundle resources) throws IOException {
        return FXMLLoader.load(location,
                null,
                new JavaFXBuilderFactory(),
                injector::getInstance);
    }
}
