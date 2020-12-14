package controller;

import controller.dialog.MonthlyBudgetEditController;
import controller.dialog.OperationEditController;
import dao.MonthlyBudgetDao;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Category;
import model.MonthlyBudget;
import model.Operation;
import model.OperationType;
import service.FxmlLoaderService;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MonthlyBudgetListController extends TabController {
    MonthlyBudgetDao monthlyBudgetDao;
    FxmlLoaderService fxmlLoaderService;

    // buttons
    @FXML
    Button addButton;
    @FXML
    Button editButton;
    @FXML
    Button deleteButton;

    // table
    @FXML
    TableView<MonthlyBudget> monthlyBudgetTableView;
    @FXML
    TableColumn<MonthlyBudget, String> nameCol;
    @FXML
    TableColumn<MonthlyBudget, Date> firstDayCol;
    @FXML
    TableColumn<MonthlyBudget, Date> lastDayCol;
    @FXML
    TableColumn<MonthlyBudget, BigDecimal> initialBalanceCol;
    @FXML
    TableColumn<MonthlyBudget, BigDecimal> currBalanceCol;

    @Inject
    public MonthlyBudgetListController(final FxmlLoaderService fxmlLoaderService, MonthlyBudgetDao monthlyBudgetDao) {
        this.monthlyBudgetDao = monthlyBudgetDao;
        this.fxmlLoaderService = fxmlLoaderService;
    }

    @FXML
    public void initialize() {
        nameCol.prefWidthProperty().bind(monthlyBudgetTableView.widthProperty().divide(5));
        firstDayCol.prefWidthProperty().bind(monthlyBudgetTableView.widthProperty().divide(5));
        lastDayCol.prefWidthProperty().bind(monthlyBudgetTableView.widthProperty().divide(5));
        initialBalanceCol.prefWidthProperty().bind(monthlyBudgetTableView.widthProperty().divide(5));
        currBalanceCol.prefWidthProperty().bind(monthlyBudgetTableView.widthProperty().divide(5));

        monthlyBudgetTableView.setRowFactory(tv -> {
            TableRow<MonthlyBudget> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && !row.isEmpty()) {
                    MonthlyBudget mb = row.getItem();
                    tabAreaController.openTab(
                            fxmlLoaderService.getLoader(getClass().getResource("/view/CategoryBudgetList.fxml")),
                            "[MB] " + mb.getName(),
                            mb.getId());
                }
            });
            return row;
        });
        monthlyBudgetTableView.setItems(FXCollections.observableArrayList(monthlyBudgetDao.findAll()));

        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        firstDayCol.setCellValueFactory(cellData -> cellData.getValue().firstDayProperty());
        firstDayCol.setCellFactory(column -> new TableCell<>() {
            private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : format.format(item));
            }
        });

        lastDayCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().lastDay()));
        lastDayCol.setCellFactory(column -> new TableCell<>() {
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
        initialBalanceCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().initialBalance()));
        currBalanceCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().currentBalance()));


        addButton.setOnAction(this::onAddAction);
        editButton.setOnAction(this::onEditAction);
        deleteButton.setOnAction(this::onDeleteAction);
    }

    private void onEditAction(ActionEvent event) {
        Stage stage = new Stage();
        MonthlyBudget mb = monthlyBudgetTableView.getSelectionModel().getSelectedItem();
        FXMLLoader loader = fxmlLoaderService.getLoader(getClass().getResource("/view/dialog/MonthlyBudgetEdit.fxml"));
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root, 600 ,400));
            MonthlyBudgetEditController controller = loader.getController();
            controller.setStage(stage);
            controller.setModel(mb);
            controller.setConfirmButtonText("confirm");

            stage.setTitle("Monthly budget edit");
            stage.showAndWait();

            if(controller.isApproved()) {
                try {
                    monthlyBudgetDao.update(mb);
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
        Stage stage = new Stage();
        MonthlyBudget mb = new MonthlyBudget();
        FXMLLoader loader = fxmlLoaderService.getLoader(getClass().getResource("/view/dialog/MonthlyBudgetEdit.fxml"));
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root, 600 ,400));
            MonthlyBudgetEditController controller = loader.getController();
            controller.setStage(stage);
            controller.setModel(mb);

            stage.setTitle("New monthly budget creation");
            stage.showAndWait();
            if(controller.isApproved()) {
                try {
                    monthlyBudgetDao.save(mb);
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
        monthlyBudgetTableView.getSelectionModel().getSelectedItems().forEach(mb -> monthlyBudgetDao.delete(mb));
        refreshList();
    }

    private void refreshList() {
        monthlyBudgetTableView.refresh();
        monthlyBudgetTableView.setItems(FXCollections.observableArrayList(monthlyBudgetDao.findAll()));
    }

    @Override
    public void onSelected() {
        refreshList();
    }
}
