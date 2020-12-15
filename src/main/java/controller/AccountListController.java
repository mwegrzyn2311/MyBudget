package controller;

import controller.dialog.AccountEditController;
import controller.dialog.MonthlyBudgetEditController;
import dao.AccountDao;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Account;
import model.MonthlyBudget;
import model.Operation;
import service.FxmlLoaderService;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.io.IOException;
import java.math.BigDecimal;

public class AccountListController extends TabController {
    AccountDao accountDao;
    FxmlLoaderService fxmlLoaderService;

    @FXML
    Button addButton;
    @FXML
    Button editButton;
    @FXML
    Button deleteButton;

    @FXML
    TableView<Account> accountTableView;
    @FXML
    TableColumn<Account, String> nameCol;
    @FXML
    TableColumn<Account, String> accountNumberCol;
    @FXML
    TableColumn<Account, BigDecimal> currBalanceCol;
    @FXML
    TableColumn<Account, BigDecimal> initialBalanceCol;

    @Inject
    public AccountListController(final FxmlLoaderService fxmlLoaderService, AccountDao accountDao) {
        this.accountDao = accountDao;
        this.fxmlLoaderService = fxmlLoaderService;
    }

    @FXML
    private void initialize() {
        // Columns auto resize on window resize
        nameCol.prefWidthProperty().bind(accountTableView.widthProperty().divide(3));
        accountNumberCol.prefWidthProperty().bind(accountTableView.widthProperty().divide(3));
        currBalanceCol.prefWidthProperty().bind(accountTableView.widthProperty().divide(6));
        initialBalanceCol.prefWidthProperty().bind(accountTableView.widthProperty().divide(6));

        accountTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //
        accountTableView.setRowFactory(tv -> {
            TableRow<Account> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() ==2 && !row.isEmpty()) {
                    Account account = row.getItem();
                    tabAreaController.openTab(
                            fxmlLoaderService.getLoader(getClass().getResource("/view/OperationList.fxml")),
                            "[Account] "+account.getName(),
                            account.getId());
                }
            });
            return row;
        });
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        accountNumberCol.setCellValueFactory(cellData -> cellData.getValue().accountNumberProperty());
        currBalanceCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData
                .getValue()
                .getInitialBalance()
                .add(cellData.getValue()
                        .getOperations()
                        .stream()
                        .map(Operation::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))));
        initialBalanceCol.setCellValueFactory(cellData -> cellData.getValue().initialBalanceProperty());


        accountTableView.setItems(FXCollections.observableArrayList(accountDao.findAll()));

        deleteButton.disableProperty().bind(
                Bindings.isEmpty(
                        accountTableView.getSelectionModel()
                                .getSelectedItems()));
        editButton.disableProperty().bind(
                Bindings.size(
                        accountTableView.getSelectionModel()
                                .getSelectedItems())
                        .isNotEqualTo(1));

        addButton.setOnAction(this::onAddAction);
        editButton.setOnAction(this::onEditAction);
        deleteButton.setOnAction(this::onDeleteAction);
    }

    private void onAddAction(ActionEvent event) {
        Stage stage = new Stage();
        Account account = new Account();
        FXMLLoader loader = fxmlLoaderService.getLoader(getClass().getResource("/view/dialog/AccountEdit.fxml"));
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root, 600 ,400));
            AccountEditController controller = loader.getController();
            controller.setStage(stage);
            controller.setModel(account);

            stage.setTitle("New account creation");
            stage.showAndWait();
            if(controller.isApproved()) {
                try {
                    accountDao.save(account);
                    refreshList();
                } catch (PersistenceException e) {
                    e.printStackTrace();
                }
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    private void onEditAction(ActionEvent event) {
        Stage stage = new Stage();
        Account account = accountTableView.getSelectionModel().getSelectedItem();
        FXMLLoader loader = fxmlLoaderService.getLoader(getClass().getResource("/view/dialog/AccountEdit.fxml"));
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root, 600 ,400));
            AccountEditController controller = loader.getController();
            controller.setStage(stage);
            controller.setModel(account);
            controller.setConfirmButtonText("confirm");

            stage.setTitle("Account edit");
            stage.showAndWait();

            if(controller.isApproved()) {
                try {
                    accountDao.update(account);
                    refreshList();
                } catch (PersistenceException e) {
                    e.printStackTrace();
                }
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    private void onDeleteAction(ActionEvent event) {
        accountTableView.getSelectionModel().getSelectedItems().forEach(account -> accountDao.delete(account));
        refreshList();
    }

    public void refreshList() {
        accountTableView.refresh();
        accountTableView.setItems(FXCollections.observableArrayList(accountDao.findAll()));
    }

    public void refreshAccountList() {
        accountTableView.setItems(FXCollections.observableArrayList(accountDao.findAll()));
        accountTableView.refresh();
    }

    @Override
    public void onSelected() {
        refreshAccountList();
    }
}
