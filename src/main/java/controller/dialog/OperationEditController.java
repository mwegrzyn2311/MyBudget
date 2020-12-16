package controller.dialog;

import controller.CategoriesViewController;
import controller.CategoryTreeListHelper;
import dao.CategoryDao;
import dao.TopCategoryDao;
import javafx.application.Platform;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import model.*;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

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

    ValidationSupport validationSupport = new ValidationSupport();

    final private TopCategoryDao topCategoryDao;

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
        try {
            if(category.getTopCategory().getOperationType().compareTo(OperationType.Income)==0){
                operation.setAmount((BigDecimal) decimalFormatter.parse(amountField.getText()));
            } else {
                BigDecimal amount = (BigDecimal) decimalFormatter.parse(amountField.getText());
                operation.setAmount(amount.negate());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        LocalDate localDate = dateField.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        operation.setDate(Date.from(instant));

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

        Date date = operation.getDate();
        if(date != null) {
            dateField.setValue(date
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate());
        }

        commentField.setText(operation.getComment());
    }
}
