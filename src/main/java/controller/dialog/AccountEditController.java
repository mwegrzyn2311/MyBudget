package controller.dialog;

import controller.AccountListController;
import dao.AccountDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.Account;
import model.MonthlyBudget;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import service.FxmlLoaderService;

import javax.inject.Inject;
import javax.swing.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

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
        validationSupport.setErrorDecorationEnabled(false);
        confirmButton.addEventHandler(ActionEvent.ACTION, e -> {
            validationSupport.setErrorDecorationEnabled(true);
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
