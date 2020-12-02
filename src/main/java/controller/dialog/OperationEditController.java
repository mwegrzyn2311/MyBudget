package controller.dialog;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Operation;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class OperationEditController extends DialogController {
    Operation operation;

    @FXML
    Button confirmButton;
    @FXML
    TextField amountField;
    @FXML
    DatePicker dateField;
    @FXML
    TextArea commentField;

    @FXML
    private void initialize() {
        confirmButton.addEventHandler(ActionEvent.ACTION, e -> {
            updateModel();
            approved = true;
            stage.close();
         });
    }

    public void setModel(Operation operation) {
        this.operation = operation;
        updateControls();
    }

    public void setConfirmButtonText(String text) {
        confirmButton.setText(text);
    }

    private void updateModel() {
        DecimalFormat decimalFormatter= new DecimalFormat();
        decimalFormatter.setParseBigDecimal(true);
        try {
            operation.setAmount((BigDecimal) decimalFormatter.parse(amountField.getText()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        LocalDate localDate = dateField.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        operation.setDate(Date.from(instant));

        operation.setComment(commentField.getText());
    }

    private void updateControls() {
        BigDecimal amount = operation.getAmount();
        if(amount != null) {
            amountField.setText(amount.toString());
        }

        Date date = operation.getDate();
        if(date != null) {
            dateField.setValue(date
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate());
        }

        commentField.setText(operation.getComment());
    }
}
