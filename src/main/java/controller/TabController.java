package controller;

import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public abstract class TabController {
    private ITabAreaController tabAreaController;
    public void setTabAreaController(ITabAreaController tabAreaController) {
        this.tabAreaController = tabAreaController;
    }
}
