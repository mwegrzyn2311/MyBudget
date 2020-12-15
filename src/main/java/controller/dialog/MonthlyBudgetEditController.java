package controller.dialog;

import controller.MonthlyBudgetListController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import model.MonthlyBudget;
import model.Operation;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class MonthlyBudgetEditController extends DialogController {
    @FXML
    TextField nameField;
    @FXML
    DatePicker dateField;

    private MonthlyBudget mb;

    @FXML
    public void initialize() {
        confirmButton.addEventHandler(ActionEvent.ACTION, e -> {
            updateModel();
            approved = true;
            stage.close();
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
