package controller.dialog;

import helper.CategoryTreeListHelper;
import dao.TopCategoryDao;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.*;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

public class CategoryBudgetEditController extends DialogController {

    private CategoryBudget catBud;

    @FXML
    TreeView<BaseCategory> categoryPicker;
    @FXML
    TextField initialBudgetField;

    final private TopCategoryDao topCategoryDao;

    @Inject
    public CategoryBudgetEditController(TopCategoryDao topCategoryDao){
        this.topCategoryDao = topCategoryDao;
    }

    @FXML
    private void initialize() {
        // TODO: Extract to abstract class
        confirmButton.addEventHandler(ActionEvent.ACTION, e -> {
            if(!initialBudgetField.getText().isEmpty()) {
                updateModel();
                approved = true;
                stage.close();
            }
        });

        final TreeItem<BaseCategory> root = CategoryTreeListHelper.createTreeView(topCategoryDao.findAll(), false);
        categoryPicker.setRoot(root);
        categoryPicker.setShowRoot(false);

        categoryPicker.getSelectionModel().selectionModeProperty().set(SelectionMode.SINGLE);

        categoryPicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isLeaf()) {
                Platform.runLater(() -> categoryPicker.getSelectionModel().clearSelection());
            }
        });

        categoryPicker.setCellFactory(CategoryTreeListHelper::buildTreeCell);

        textFieldIntoMoneyField(initialBudgetField);
    }

    public void setModel(CategoryBudget catBud) {
        this.catBud = catBud;
        updateControls();
    }

    private void updateModel() {
        final Category category = (Category) categoryPicker.getSelectionModel().getSelectedItem().getValue();
        catBud.setCategory(category);

        DecimalFormat decimalFormatter = new DecimalFormat();
        decimalFormatter.setParseBigDecimal(true);
        try {
            catBud.setInitialBudget((BigDecimal) decimalFormatter.parse(initialBudgetField.getText()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void updateControls() {
        BigDecimal initialBalance = catBud.getInitialBudget();
        if(initialBalance != null) {
            initialBudgetField.setText(initialBalance.toString());
        }
        final Category category = catBud.getCategory();
        if(category != null) {
            categoryPicker.getRoot().getChildren().stream()
                    .filter(item -> item.getValue() == category.getTopCategory())
                    .findFirst().flatMap(subtree -> subtree.getChildren().stream()
                    .filter(child -> child.getValue() == category)
                    .findFirst()).ifPresent(cat -> {
                categoryPicker.getSelectionModel().select(cat);
                categoryPicker.scrollTo(categoryPicker.getSelectionModel().getSelectedIndex());
            });
        }
    }
}
