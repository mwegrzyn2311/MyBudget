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
    AccountDao accountDao;
    FxmlLoaderService fxmlLoaderService;

    Account account;

    @FXML
    TextField nameField;
    @FXML
    TextField initialBalanceField;
    @FXML
    TextField accountNumberField;

    Optional<AccountListController> accountListController;

/*
    @Inject
    public AccountEditController(final FxmlLoaderService fxmlLoaderService, AccountDao accountDao) {
        this.accountDao = accountDao;
        this.fxmlLoaderService = fxmlLoaderService;
    }

    @FXML
    private void initialize() {
        confirmButton.addEventHandler(ActionEvent.ACTION, e -> {
            Optional<Account> newAccount = accountDao.create(nameField.getText(), accountNumberField.getText(), BigDecimal.valueOf(Double.parseDouble(initialBalanceField.getText())));
            if(newAccount.isEmpty()) {
                System.out.println("New account creation failed");
            } else {
                System.out.println("New account created");

                accountListController.ifPresent(AccountListController::refreshAccountList);
                // Close the window after new account is created
                ((Node) e.getSource()).getScene().getWindow().hide();
            }
         });

        // Text field formatters
        textFieldIntoAccountNumberField(accountNumberField);
        textFieldIntoMoneyField(initialBalanceField);
    }


    public void setAccountListController(AccountListController accountListController) {
        this.accountListController = Optional.of(accountListController);
    }
    */

    @FXML
    public void initialize() {
        textFieldIntoAccountNumberField(accountNumberField);
        textFieldIntoMoneyField(initialBalanceField);

        confirmButton.addEventHandler(ActionEvent.ACTION, e -> {
            updateModel();
            approved = true;
            stage.close();
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
