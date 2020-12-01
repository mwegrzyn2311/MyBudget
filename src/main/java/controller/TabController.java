package controller;

import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public abstract class TabController {
    protected ITabAreaController tabAreaController;
    public void setTabAreaController(ITabAreaController tabAreaController) {
        this.tabAreaController = tabAreaController;
    }

    public void setTabParameter(Object param) {

    }
}
