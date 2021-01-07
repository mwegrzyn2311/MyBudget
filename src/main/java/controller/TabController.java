package controller;

public abstract class TabController {
    protected ITabAreaController tabAreaController;
    public void setTabAreaController(ITabAreaController tabAreaController) {
        this.tabAreaController = tabAreaController;
    }

    public void setTabParameter(Object param) {

    }

    public abstract void onSelected();
}
