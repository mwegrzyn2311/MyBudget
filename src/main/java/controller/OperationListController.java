package controller;

import controller.dialog.OperationEditController;
import dao.AccountDao;
import dao.OperationDao;
import javafx.beans.binding.Bindings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import model.Account;
import model.Category;
import model.Operation;
import model.OperationType;
import service.FxmlLoaderService;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OperationListController extends TabController {

    FxmlLoaderService fxmlLoaderService;
    AccountDao accountDao;
    OperationDao operationDao;

    Account account;

    @FXML
    Button addButton;
    @FXML
    Button deleteButton;
    @FXML
    Button editButton;


    @FXML
    TableView<Operation> operationTableView;
    @FXML
    TableColumn<Operation, BigDecimal> amountColumn;
    @FXML
    TableColumn<Operation, Date> dateColumn;
    @FXML
    TableColumn<Operation, String> commentColumn;
    @FXML
    TableColumn<Operation, Category> categoryColumn;

    @FXML
    public TextFlow name;
    @FXML
    public TextFlow currentBalance;

    @Inject
    public OperationListController(FxmlLoaderService fxmlLoaderService, AccountDao accountDao, OperationDao operationDao) {
        this.fxmlLoaderService = fxmlLoaderService;
        this.accountDao = accountDao;
        this.operationDao = operationDao;
    }

    @FXML
    private void initialize() {
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
        amountColumn.setCellFactory(column -> {
            TableCell<Operation, BigDecimal> cell = new TableCell<>() {
                @Override
                protected void updateItem(BigDecimal item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : getItem().toString());
                    setGraphic(null);
                }
            };
            return cell;
        });

        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        dateColumn.setCellFactory(column -> new TableCell<Operation, Date>() {
            private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if(empty) {
                    setText(null);
                }
                else {
                    setText(format.format(item));
                }
            }
        });
        commentColumn.setCellValueFactory(cellData -> cellData.getValue().commentProperty());

        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        categoryColumn.setCellFactory(column -> {
            return new TableCell<Operation, Category>() {
                @Override
                protected void updateItem(Category item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : getItem().getTopCategory().getName()+"/"+getItem().getName());
                    setGraphic(null);
                    TableRow<Operation> currentRow = getTableRow();
                    if (!isEmpty()) {
                        int comparison = item.getTopCategory().getOperationType().compareTo(OperationType.Income);
                        if(comparison == 0)
                            currentRow.setStyle("-fx-background-color:lightgreen");
                        else currentRow.setStyle("-fx-background-color:orangered");
                    }
                }
            };
        });

        deleteButton.disableProperty().bind(
                Bindings.isEmpty(
                        operationTableView.getSelectionModel()
                                .getSelectedItems()));
        editButton.disableProperty().bind(
                Bindings.size(
                        operationTableView.getSelectionModel()
                                .getSelectedItems())
                        .isNotEqualTo(1));
        operationTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        addButton.setOnAction(this::onAddAction);
        editButton.setOnAction(this::onEditAction);
        deleteButton.setOnAction(this::onDeleteAction);
    }

    private void refreshList() {
        accountDao.refresh(account);
        if(account != null) {
            operationTableView.refresh();
            name.getChildren().clear();
            name.getChildren().add(new Text(account.getName()));
            currentBalance.getChildren().clear();
            currentBalance.getChildren().add(new Text(account.getInitialBalance()
                    .add(account.getOperations().stream().map(Operation::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)).toString()));
            operationTableView.setItems(account.operationsObservableList());
        }
    }

    private void onDeleteAction(ActionEvent event) {
        for(Operation operation : operationTableView.getSelectionModel().getSelectedItems()) {
            account.removeOperation(operation);
        }
        accountDao.save(account);

        refreshList();
    }

    private void onEditAction(ActionEvent event) {
        Operation operation = operationTableView.getSelectionModel().getSelectedItem();
        Stage stage = new Stage();

        FXMLLoader loader = fxmlLoaderService.getLoader(getClass().getResource("/view/dialog/OperationEdit.fxml"));
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root, 600 ,400));
            OperationEditController controller = loader.getController();
            controller.setStage(stage);
            controller.setModel(operation);
            controller.setConfirmButtonText("Confirm");

            stage.setTitle("Edit operation creation");
            stage.showAndWait();

            if(controller.isApproved()) {
                try {
                    operationDao.update(operation);
                    refreshList();
                } catch (PersistenceException e) {
                    e.printStackTrace();
                }
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private void onAddAction(ActionEvent event) {
        Operation operation = new Operation();
        operation.setAccount(account);

        Stage stage = new Stage();

        FXMLLoader loader = fxmlLoaderService.getLoader(getClass().getResource("/view/dialog/OperationEdit.fxml"));
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root, 600 ,400));
            OperationEditController controller = loader.getController();
            controller.setStage(stage);
            controller.setModel(operation);

            stage.setTitle("New operation creation");
            stage.showAndWait();

            if(controller.isApproved()) {
                try {
                    account.addOperation(operation);
                    operationDao.save(operation);
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

    @Override
    public void setTabParameter(Object param) {
        Long accountId = (Long) param;
        account = accountDao.findOne(accountId).orElse(null);
        if(account != null) {
            operationTableView.setItems(account.operationsObservableList());
            name.getChildren().add(new Text(account.getName()));
            currentBalance.getChildren().add(new Text(account.getInitialBalance()
                    .add(account.getOperations().stream().map(Operation::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)).toString()));
            operationTableView.setItems(account.operationsObservableList());
        }
    }

    @Override
    public void onSelected() {
        refreshList();
    }
}
