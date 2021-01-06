package controller;

import controller.dialog.CategoryBudgetEditController;
import dao.CategoryBudgetDao;
import dao.MonthlyBudgetDao;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import model.*;
import service.FxmlLoaderService;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.io.IOException;
import java.math.BigDecimal;

public class CategoryBudgetListController extends TabController {
    FxmlLoaderService fxmlLoaderService;
    MonthlyBudgetDao monthlyBudgetDao;
    CategoryBudgetDao categoryBudgetDao;

    MonthlyBudget mb;

    @FXML
    Button addButton;
    @FXML
    Button deleteButton;
    @FXML
    Button editButton;


    @FXML
    TableView<CategoryBudget> categoryBudgetTableView;
    @FXML
    TableColumn<CategoryBudget, Category> categoryCol;
    @FXML
    TableColumn<CategoryBudget, BigDecimal> initialBalanceCol;
    @FXML
    TableColumn<CategoryBudget, BigDecimal> currBalanceCol;

    @FXML
    public TextFlow mbName;

    @Inject
    public CategoryBudgetListController(FxmlLoaderService fxmlLoaderService, MonthlyBudgetDao monthlyBudgetDao, CategoryBudgetDao categoryBudgetDao) {
        this.fxmlLoaderService = fxmlLoaderService;
        this.monthlyBudgetDao = monthlyBudgetDao;
        this.categoryBudgetDao = categoryBudgetDao;
    }

    @FXML
    private void initialize() {
        categoryCol.prefWidthProperty().bind(categoryBudgetTableView.widthProperty().divide(2));
        initialBalanceCol.prefWidthProperty().bind(categoryBudgetTableView.widthProperty().divide(4));
        currBalanceCol.prefWidthProperty().bind(categoryBudgetTableView.widthProperty().divide(4));

        initialBalanceCol.setCellValueFactory(cellData -> cellData.getValue().initialBudgetProperty());
        initialBalanceCol.setCellFactory(column -> {
            TableCell<CategoryBudget, BigDecimal> cell = new TableCell<>() {
                @Override
                protected void updateItem(BigDecimal item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : getItem().toString());
                    setGraphic(null);
                }
            };
            return cell;
        });
        currBalanceCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().currentBalance()));
        currBalanceCol.setCellFactory(column -> {
            TableCell<CategoryBudget, BigDecimal> cell = new TableCell<>() {
                @Override
                protected void updateItem(BigDecimal item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : getItem().toString());
                    setGraphic(null);
                    TableRow<CategoryBudget> currentRow = getTableRow();
                    if (!isEmpty()) {
                        int comparison = item.compareTo(BigDecimal.ZERO);
                        if(comparison >= 0)
                            currentRow.setStyle("-fx-background-color:lightgreen");
                        else currentRow.setStyle("-fx-background-color:orangered");
                    }
                }
            };
            return cell;
        });


        categoryCol.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        categoryCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : getItem().getTopCategory().getName() + "/" + getItem().getName());
                setGraphic(null);
            }
        });

        deleteButton.disableProperty().bind(
                Bindings.isEmpty(
                        categoryBudgetTableView.getSelectionModel()
                                .getSelectedItems()));
        editButton.disableProperty().bind(
                Bindings.size(
                        categoryBudgetTableView.getSelectionModel()
                                .getSelectedItems())
                        .isNotEqualTo(1));
        categoryBudgetTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        addButton.setOnAction(this::onAddAction);
        editButton.setOnAction(this::onEditAction);
        deleteButton.setOnAction(this::onDeleteAction);
    }

    private void refreshList() {
        monthlyBudgetDao.refresh(mb);
        if(mb != null) {
            categoryBudgetTableView.refresh();
            mbName.getChildren().clear();
            setTextFloats();
            categoryBudgetTableView.setItems(mb.categoryBudgetsObservableList());
        }
    }

    private void onDeleteAction(ActionEvent event) {
        mb.getCategoryBudgets()
                .removeAll(categoryBudgetTableView.getSelectionModel().getSelectedItems());
        monthlyBudgetDao.save(mb);

        refreshList();
    }

    private void onEditAction(ActionEvent event) {
        CategoryBudget categoryBudget = categoryBudgetTableView.getSelectionModel().getSelectedItem();
        Stage stage = new Stage();
        categoryBudget.setMonthlyBudget(mb);

        FXMLLoader loader = fxmlLoaderService.getLoader(getClass().getResource("/view/dialog/CategoryBudgetEdit.fxml"));
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root, 600 ,400));
            CategoryBudgetEditController controller = loader.getController();
            controller.setStage(stage);
            controller.setModel(categoryBudget);
            controller.setConfirmButtonText("Confirm");

            stage.setTitle("Category budget edit");
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
        CategoryBudget categoryBudget = new CategoryBudget();
        Stage stage = new Stage();
        categoryBudget.setMonthlyBudget(mb);

        FXMLLoader loader = fxmlLoaderService.getLoader(getClass().getResource("/view/dialog/CategoryBudgetEdit.fxml"));
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root, 600 ,400));
            CategoryBudgetEditController controller = loader.getController();
            controller.setStage(stage);
            controller.setModel(categoryBudget);

            stage.setTitle("New category budget creation");
            stage.showAndWait();

            if(controller.isApproved()) {
                try {
                    mb.addCategoryBudget(categoryBudget);
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

    @Override
    public void setTabParameter(Object param) {
        Long accountId = (Long) param;
        mb = monthlyBudgetDao.findOne(accountId).orElse(null);
        if(mb != null) {
            setTextFloats();
            categoryBudgetTableView.setItems(mb.categoryBudgetsObservableList());
        }
    }

    @Override
    public void onSelected() {
        refreshList();
    }

    private void setTextFloats(){
        Text text = new Text(mb.getName());
        text.setFill(Color.web("0xffb703"));
        text.setStyle("-fx-font-weight: bold");
        mbName.getChildren().add(text);
    }
}
