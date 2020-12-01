package controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import service.FxmlLoaderService;

import java.io.IOException;
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

    @Inject
    public MyBudgetAppController(final FxmlLoaderService fxmlLoaderService) {
        this.fxmlLoaderService = fxmlLoaderService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainArea.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

        accountsLink.setOnAction(event -> {
            openTab(fxmlLoaderService.getLoader(getClass().getResource("/view/AccountList.fxml")), "Account list");
        });

        openTab(fxmlLoaderService.getLoader(getClass().getResource("/view/AccountList.fxml")), "Account list");
    }

    public void openTab(FXMLLoader loader, String name, Object param) {
        if(openTabs.containsKey(name)) {
            mainArea.getSelectionModel()
                    .select(openTabs.get(name));
        } else {
            Tab newTab = new Tab();
            newTab.setText(name);
            try {
                newTab.setContent(loader.load());
            } catch(IOException ex) {
                ex.printStackTrace();
            }
            TabController tabController = (TabController)loader.getController();
            tabController.setTabAreaController(this);
            tabController.setTabParameter(param);

            mainArea.getTabs().add(newTab);
            openTabs.put(name, newTab);
            mainArea.getSelectionModel().select(newTab);

            newTab.setOnSelectionChanged(event -> tabController.onSelected());
            newTab.setOnClosed(event -> openTabs.remove(name));
        }
    }
}
