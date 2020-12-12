package controller.dialog;

import controller.CategoriesViewController;
import dao.CategoryDao;
import dao.TopCategoryDao;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Category;
import model.Operation;
import model.OperationType;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class OperationEditController extends DialogController {
    Operation operation;

    @FXML
    Button confirmButton;
    @FXML
    TextField amountField;
    @FXML
    DatePicker dateField;
    @FXML
    TextArea commentField;
    @FXML
    TreeView<String> categoryPicker;

    private TopCategoryDao topCategoryDao;
    private CategoryDao categoryDao;

    private final TreeItem<String> root = new TreeItem<>("Categories");

    @Inject
    public OperationEditController(TopCategoryDao topCategoryDao, CategoryDao categoryDao){
        this.topCategoryDao = topCategoryDao;
        this.categoryDao = categoryDao;
    }

    @FXML
    private void initialize() {
        confirmButton.addEventHandler(ActionEvent.ACTION, e -> {
            updateModel();
            approved = true;
            stage.close();
         });
        root.setExpanded(false);
        CategoriesViewController.createTreeView(topCategoryDao, root);
        this.categoryPicker.setRoot(root);
        this.categoryPicker.setShowRoot(false);

        this.categoryPicker.getSelectionModel().selectionModeProperty().set(SelectionMode.SINGLE);

        this.categoryPicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isLeaf()) {
                Platform.runLater(() -> categoryPicker.getSelectionModel().clearSelection());
            }
        });

        // Text field formatters
        textFieldIntoMoneyField(amountField);
    }

    public void setModel(Operation operation) {
        this.operation = operation;
        updateControls();
    }

    public void setConfirmButtonText(String text) {
        confirmButton.setText(text);
    }

    private void updateModel() {
        Category category = categoryDao.findByName(categoryPicker.getSelectionModel().getSelectedItem()
                .getValue()).get();
        operation.setCategory(category);

        operation.setCategory(category);

        DecimalFormat decimalFormatter= new DecimalFormat();
        decimalFormatter.setParseBigDecimal(true);
        try {
            if(category.getTopCategory().getOperationType().compareTo(OperationType.Income)==0){
                operation.setAmount((BigDecimal) decimalFormatter.parse(amountField.getText()));}
            else{
                BigDecimal amount = (BigDecimal) decimalFormatter.parse(amountField.getText());
                operation.setAmount(amount.negate());}
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

        Date date = operation.getDate();
        if(date != null) {
            dateField.setValue(date
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate());
        }

        commentField.setText(operation.getComment());

        Category category = operation.getCategory();
        if(category!=null){
            getTreeViewItem(root, category.getTopCategory().getName()).setExpanded(true);
            categoryPicker.getSelectionModel().select(categoryPicker.
                    getRow(getTreeViewItem(root, category.getName())));
        }
    }
}
