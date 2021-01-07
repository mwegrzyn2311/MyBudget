package controller.dialog;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import model.OperationType;
import model.TopCategory;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.util.Arrays;

public class TopCategoryAddController extends DialogController{
    TopCategory topCategory;

    ValidationSupport validationSupport = new ValidationSupport();

    @FXML
    public ChoiceBox<OperationType> typeChoice;
    @FXML
    public TextField nameChoice;

    @FXML
    private void initialize() {
        validationSupport.registerValidator(typeChoice, true, Validator.createEmptyValidator("Type is required"));
        validationSupport.registerValidator(nameChoice, true, Validator.createEmptyValidator("Name is required"));
        // Force validation redecoration
        validationSupport.initInitialDecoration();

        confirmButton.disableProperty().bind(validationSupport.invalidProperty());

        confirmButton.addEventHandler(ActionEvent.ACTION, e -> {

            if(!validationSupport.isInvalid()) {
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
