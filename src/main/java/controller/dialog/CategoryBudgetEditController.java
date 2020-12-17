package controller.dialog;

import controller.CategoryTreeListHelper;
import dao.TopCategoryDao;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.*;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.w3c.dom.Text;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class CategoryBudgetEditController extends DialogController {
    private CategoryBudget catBud;

    ValidationSupport validationSupport = new ValidationSupport();

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

        validationSupport.registerValidator(initialBudgetField, true, Validator.createEmptyValidator("Initial budget is required"));
        validationSupport.registerValidator(categoryPicker, true, Validator.createEmptyValidator("Category is required"));
        confirmButton.disableProperty().bind(validationSupport.invalidProperty());
        // Force validation redecoration
        validationSupport.initInitialDecoration();

        confirmButton.addEventHandler(ActionEvent.ACTION, e -> {
            if(!validationSupport.isInvalid()) {
                updateModel();
                approved = true;
                stage.close();
            }
        });
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
