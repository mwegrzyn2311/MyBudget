package controller;

import dao.AccountDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Account;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Optional;

public class AccountListController extends TabController {
    AccountDao accountDao;

    @FXML
    Button addButton;
    @FXML
    TableView<Account> accountTableView;
    @FXML
    TableColumn<Account, String> nameColumn;
    @FXML
    TableColumn<Account, BigDecimal> currBalanceColumn;

    @Inject
    public AccountListController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        currBalanceColumn.setCellValueFactory(cellData -> cellData.getValue().initialBalanceProperty());

        accountTableView.setItems(FXCollections.observableArrayList(accountDao.findAll()));
    }
}
