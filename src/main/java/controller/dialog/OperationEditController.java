package controller.dialog;

import helper.CategoryTreeListHelper;
import dao.TopCategoryDao;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.*;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;

public class OperationEditController extends DialogController {
    Operation operation;


    @FXML
    TextField amountField;
    @FXML
    DatePicker dateField;
    @FXML
    TextArea commentField;
    @FXML
    TreeView<BaseCategory> categoryPicker;

    private final ValidationSupport validationSupport = new ValidationSupport();

    private final TopCategoryDao topCategoryDao;

    @Inject
    public OperationEditController(TopCategoryDao topCategoryDao){
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

        // Text field formatters
        textFieldIntoMoneyField(amountField);

        validationSupport.registerValidator(commentField, true, Validator.createEmptyValidator("Comment is required"));
        validationSupport.registerValidator(amountField, true, Validator.createEmptyValidator("Amount is required"));
        validationSupport.registerValidator(categoryPicker, true, Validator.createEmptyValidator("Category is required"));
        validationSupport.registerValidator(dateField, true, Validator.createEmptyValidator("Operation date is required"));
        // Force validation redecoration
        validationSupport.initInitialDecoration();

        confirmButton.disableProperty().bind(validationSupport.invalidProperty());

        confirmButton.addEventHandler(ActionEvent.ACTION, e -> {
            if(!validationSupport.isInvalid()) {
                updateModel();
                approved = true;
                stage.close();
            }
        });
    }

    public void setModel(Operation operation) {
        this.operation = operation;
        updateControls();
    }

    private void updateModel() {
        final Category category = (Category) categoryPicker.getSelectionModel().getSelectedItem().getValue();
        operation.setCategory(category);

        DecimalFormat decimalFormatter = new DecimalFormat();
        decimalFormatter.setParseBigDecimal(true);

        if(category.getTopCategory().getOperationType().compareTo(OperationType.Income)==0){
            operation.setAmount(BigDecimal.valueOf(Double.parseDouble(amountField.getText())));
        } else {
            BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(amountField.getText()));
            operation.setAmount(amount.negate());
        }

        operation.setDate(dateField.getValue());

        operation.setComment(commentField.getText());
    }

    private void updateControls() {
        BigDecimal amount = operation.getAmount();
        if(amount != null) {
            amountField.setText(amount.abs().toString());
        }
        final Category category = operation.getCategory();
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

        LocalDate date = operation.getDate();
        if(date != null) {
            dateField.setValue(date);
        }

        commentField.setText(operation.getComment());
    }
}
