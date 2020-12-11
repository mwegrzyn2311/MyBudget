package controller.dialog;

import controller.CategoriesViewController;
import dao.TopCategoryDao;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.OperationType;
import model.TopCategory;

import javax.inject.Inject;
import java.util.Arrays;

public class TopCategoryAddController extends DialogController{

    TopCategory topCategory;
    @FXML
    public Button createButton;
    @FXML
    public ChoiceBox<OperationType> typeChoice;
    @FXML
    public TextField nameChoice;
    
    private final TopCategoryDao topCategoryDao;
    
    @Inject
    public TopCategoryAddController(TopCategoryDao topCategoryDao){
        this.topCategoryDao = topCategoryDao;
    }

    @FXML
    private void initialize() {
        createButton.addEventHandler(ActionEvent.ACTION, e -> {
            if(!nameChoice.getText().isEmpty()) {
                addTopCategory();
                approved = true;
                stage.close();
            }
        });
        typeChoice.getItems().addAll(Arrays.asList(OperationType.values()));
    }

    private void addTopCategory() {
        topCategory.setName(nameChoice.getText());
        topCategory.setOperationType(typeChoice.getValue());
    }

    public void setModel(TopCategory topCategory) {
        this.topCategory = topCategory;
        updateControls();
    }

    private void updateControls() {
        this.nameChoice.setText(topCategory.getName());
        OperationType operationType = topCategory.getOperationType();
        if(operationType!=null){
            typeChoice.setValue(operationType);
        }
    }
}
