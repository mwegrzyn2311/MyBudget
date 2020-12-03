package controller.dialog;

import controller.AccountListController;
import dao.AccountDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.Account;
import service.FxmlLoaderService;

import javax.inject.Inject;
import javax.swing.*;
import java.math.BigDecimal;
import java.util.Optional;

public class AccountCreationController extends DialogController {
    AccountDao accountDao;
    FxmlLoaderService fxmlLoaderService;

    @FXML
    Button createButton;
    @FXML
    TextField nameField;
    @FXML
    TextField initialBalanceField;
    @FXML
    TextField accountNumberField;

    Optional<AccountListController> accountListController;


    @Inject
    public AccountCreationController(final FxmlLoaderService fxmlLoaderService, AccountDao accountDao) {
        this.accountDao = accountDao;
        this.fxmlLoaderService = fxmlLoaderService;
    }

    @FXML
    private void initialize() {
        createButton.addEventHandler(ActionEvent.ACTION, e -> {
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
}
