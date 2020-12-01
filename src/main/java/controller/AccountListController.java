package controller;

import dao.AccountDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import model.Account;
import service.FxmlLoaderService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Optional;

public class AccountListController extends TabController {
    AccountDao accountDao;
    FxmlLoaderService fxmlLoaderService;

    @FXML
    Button addButton;
    @FXML
    TableView<Account> accountTableView;
    @FXML
    TableColumn<Account, String> nameColumn;
    @FXML
    TableColumn<Account, BigDecimal> currBalanceColumn;

    @Inject
    public AccountListController(final FxmlLoaderService fxmlLoaderService, AccountDao accountDao) {
        this.accountDao = accountDao;
        this.fxmlLoaderService = fxmlLoaderService;
    }

    @FXML
    private void initialize() {
        accountTableView.setRowFactory(tv -> {
            TableRow<Account> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() ==2 && !row.isEmpty()) {
                    Account account = row.getItem();
                    tabAreaController.openTab(
                            fxmlLoaderService.getLoader(getClass().getResource("/view/OperationList.fxml")),
                            "Account "+account.getName(),
                            account.getId());
                }
            });
            return row;
        });
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        currBalanceColumn.setCellValueFactory(cellData -> cellData.getValue().initialBalanceProperty());


        accountTableView.setItems(FXCollections.observableArrayList(accountDao.findAll()));
    }
}
