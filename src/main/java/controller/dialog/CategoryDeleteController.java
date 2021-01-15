package controller.dialog;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class CategoryDeleteController extends DialogController {
    @FXML
    Button denyButton;

    public CategoryDeleteController() {
    }

    @FXML
    public void initialize(){
        confirmButton.addEventHandler(ActionEvent.ACTION, e -> {
                approved = true;
                stage.close();
            }
        );

        denyButton.addEventHandler(ActionEvent.ACTION, e -> {
            approved = false;
            stage.close();
        });
    }
}
