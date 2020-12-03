package controller.dialog;

import javafx.scene.control.TextField;
import javafx.stage.Stage;


public abstract class DialogController {
    protected boolean approved = false;
    protected Stage stage;

    public boolean isApproved() {
        return approved;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    protected void textFieldIntoAccountNumberField(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("(\\d{4} )+(\\d{0,4})")) {
                newValue = newValue.replaceAll("[^\\d ]", "");
                textField.setText(newValue);
            }
            // Make space after each 4 digits
            if ((newValue.length()-4)%5 == 0) {
                newValue = newValue.concat(" ");
                textField.setText(newValue);
            }
        });
    }

    protected void textFieldIntoMoneyField(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("(\\d)+([,.]\\d{0,2})?")) {
                // In this case, we basically abort user changes
                textField.setText(newValue.replaceAll("[^\\d,.]", ""));
            }
        });
    }
}
