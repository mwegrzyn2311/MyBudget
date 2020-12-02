package controller;

import javafx.fxml.FXMLLoader;

public interface ITabAreaController {
    void openTab(FXMLLoader loader, String name, Object param);

    default void openTab(FXMLLoader loader, String name) {
        openTab(loader, name, null);
    }
}
