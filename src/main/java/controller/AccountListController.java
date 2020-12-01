package controller;

import dao.AccountDao;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.Account;
import model.Operation;
import service.FxmlLoaderService;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
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
    @FXML
    TableColumn<Account, BigDecimal> initialBalanceColumn;

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
        currBalanceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData
                .getValue()
                .getInitialBalance()
                .subtract(cellData.getValue()
                        .getOperations()
                        .stream()
                        .map(Operation::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))));
        initialBalanceColumn.setCellValueFactory(cellData -> cellData.getValue().initialBalanceProperty());


        accountTableView.setItems(FXCollections.observableArrayList(accountDao.findAll()));

        addButton.addEventHandler(ActionEvent.ACTION, e -> {
            try {
                FXMLLoader loader = fxmlLoaderService.getLoader(getClass().getResource("/view/AccountCreation.fxml"));
                Parent root = loader.load();
                ((AccountCreationController) loader.getController()).setAccountListController(this);
                Stage stage = new Stage();
                stage.setScene(new Scene(root, 600 ,400));
                stage.setTitle("New account creation");
                stage.show();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public void refreshAccountList() {
        accountTableView.setItems(FXCollections.observableArrayList(accountDao.findAll()));
    }
}
