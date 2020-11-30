package controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import service.FxmlLoaderService;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MyBudgetAppController implements Initializable {
    private final Map<String, Tab> openTabs = new HashMap<>();

    private final FxmlLoaderService fxmlLoaderService;

    @FXML
    private TabPane mainArea;

    //buttons
    @FXML
    private Button accountsLink;

    @FXML
    private Button operationsLink;

    @Inject
    public MyBudgetAppController(final FxmlLoaderService fxmlLoaderService) {
        this.fxmlLoaderService = fxmlLoaderService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        operationsLink.setOnAction(event -> openTab("/view/Operations.fxml", "Operations"));
    }

    private void openTab(String filename, String name) {
        if(openTabs.containsKey(filename)) {
            mainArea.getSelectionModel()
                    .select(openTabs.get(filename));
        } else {
            try {
                Tab newTab = new Tab();
                newTab.setText(name);
                newTab.setContent(fxmlLoaderService.load(this.getClass().getResource(filename)));
                mainArea.getTabs().add(newTab);
                openTabs.put(filename, newTab);
                newTab.setOnClosed(event -> openTabs.remove(filename));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
