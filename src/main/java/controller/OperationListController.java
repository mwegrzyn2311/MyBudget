package controller;

import dao.AccountDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Account;
import model.Operation;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;

public class OperationListController extends TabController {
    AccountDao accountDao;

    int accountId;

    @FXML
    Button addButton;
    @FXML
    TableView<Operation> operationTableView;
    @FXML
    TableColumn<Operation, BigDecimal> amountColumn;
    @FXML
    TableColumn<Operation, Date> dateColumn;
    @FXML
    TableColumn<Operation, String> commentColumn;

    @Inject
    public OperationListController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @FXML
    private void initialize() {
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        commentColumn.setCellValueFactory(cellData -> cellData.getValue().commentProperty());


    }

    @Override
    public void setTabParameter(Object param) {
        accountId = (Integer) param;
        operationTableView.setItems(accountDao.findById(accountId)
                .map(Account::operationsObservableList)
                .orElse(FXCollections.emptyObservableList()));
    }
}
