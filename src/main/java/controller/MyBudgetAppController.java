package controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import service.FxmlLoaderService;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MyBudgetAppController implements Initializable, ITabAreaController {
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
    }

    public void openTab(TabController tabController, String name) {
        if(openTabs.containsKey(name)) {
            mainArea.getSelectionModel()
                    .select(openTabs.get(name));
        } else {
                Tab newTab = new Tab();
                newTab.setText(name);
                newTab.setContent(tabController);

                tabController.setTabAreaController(this);

                mainArea.getTabs().add(newTab);
                openTabs.put(name, newTab);

                newTab.setOnClosed(event -> openTabs.remove(name));
        }
    }
}
