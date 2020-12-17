package service;

import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;

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
    public FXMLLoader getLoader(final URL location) {
        return getLoader(location, null);
    }

    /**
     * Create controllers using Guice to inject dependencies
     * @param location location URI
     * @param resources resources bundle
     * @return Node
     */
    public FXMLLoader getLoader(final URL location, ResourceBundle resources) {
        return new FXMLLoader(location,
                resources,
                new JavaFXBuilderFactory(),
                injector::getInstance);
    }
}
