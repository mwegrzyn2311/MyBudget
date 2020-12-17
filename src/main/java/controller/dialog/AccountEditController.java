package controller.dialog;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.Account;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.math.BigDecimal;

public class AccountEditController extends DialogController {
    Account account;

    ValidationSupport validationSupport = new ValidationSupport();

    @FXML
    TextField nameField;
    @FXML
    TextField initialBalanceField;
    @FXML
    TextField accountNumberField;

    @FXML
    public void initialize() {
        textFieldIntoAccountNumberField(accountNumberField);
        textFieldIntoMoneyField(initialBalanceField);

        validationSupport.registerValidator(nameField, true, Validator.createEmptyValidator("Account name is required"));
        validationSupport.registerValidator(initialBalanceField, true, Validator.createEmptyValidator("Initial balance is required"));
        // Force validation redecoration
        validationSupport.initInitialDecoration();

        confirmButton.disableProperty().bind(validationSupport.invalidProperty());
        confirmButton.addEventHandler(ActionEvent.ACTION, e -> {
            if(!validationSupport.isInvalid())
            {
                updateModel();
                approved = true;
                stage.close();
            }
        });
    }

    private void updateModel() {
        account.setName(nameField.getText());
        account.setInitialBalance(BigDecimal.valueOf(Double.parseDouble(initialBalanceField.getText())));
        account.setAccountNumber(accountNumberField.getText());
    }

    public void updateControls() {
        nameField.setText(account.getName());
        BigDecimal initialBalance = account.getInitialBalance();
        if(initialBalance != null) {
            initialBalanceField.setText(initialBalance.toString());
        }
        String accNumber = account.getAccountNumber();
        if(accNumber != null) {
            accountNumberField.setText(account.getAccountNumber());
        }
    }

    public void setModel(Account account) {
        this.account = account;
        updateControls();
    }
}
