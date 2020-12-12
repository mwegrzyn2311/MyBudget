package controller.dialog;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import model.OperationType;
import model.TopCategory;

import java.util.Arrays;

public class TopCategoryAddController extends DialogController{
    TopCategory topCategory;

    @FXML
    public ChoiceBox<OperationType> typeChoice;
    @FXML
    public TextField nameChoice;

    @FXML
    private void initialize() {
        confirmButton.addEventHandler(ActionEvent.ACTION, e -> {
            if(!nameChoice.getText().isEmpty()) {
                updateModel();
                approved = true;
                stage.close();
            }
        });
        typeChoice.getItems().addAll(Arrays.asList(OperationType.values()));
    }

    private void updateModel() {
        topCategory.setName(nameChoice.getText());
        topCategory.setOperationType(typeChoice.getValue());
    }

    public void setModel(TopCategory topCategory) {
        this.topCategory = topCategory;
        updateControls();
    }

    private void updateControls() {
        this.nameChoice.setText(topCategory.getName());
        OperationType operationType = topCategory.getOperationType();
        if(operationType!=null){
            typeChoice.setValue(operationType);
        }
    }
}
