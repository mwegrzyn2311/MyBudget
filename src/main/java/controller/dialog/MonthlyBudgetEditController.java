package controller.dialog;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import model.MonthlyBudget;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class MonthlyBudgetEditController extends DialogController {
    private MonthlyBudget mb;

    ValidationSupport validationSupport = new ValidationSupport();

    @FXML
    TextField nameField;
    @FXML
    DatePicker dateField;

    @FXML
    public void initialize() {
        validationSupport.registerValidator(nameField, true,  Validator.createEmptyValidator("Name is required"));
        validationSupport.registerValidator(dateField, true,  Validator.createEmptyValidator("Date is required"));
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
    }

    private void updateModel() {
        mb.setName(nameField.getText());

        LocalDate localDate = dateField.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        mb.setFirstDay(Date.from(instant));

    }

    public void updateControls() {
        nameField.setText(mb.getName());

        Date date = mb.getFirstDay();
        if(date != null) {
            dateField.setValue(date
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate());
        }
    }

    public void setModel(MonthlyBudget mb) {
        this.mb = mb;
        updateControls();
    }
}
