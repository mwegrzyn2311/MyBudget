package controller.dialog;

import dao.TopCategoryDao;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Category;
import model.TopCategory;
import service.FxmlLoaderService;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.io.IOException;

public class CategoryAddController extends DialogController{

    Category category;

    @FXML
    Button addTopButton;
    @FXML
    ChoiceBox<TopCategory> topCategorySelection;
    @FXML
    TextField name;

    private final FxmlLoaderService fxmlLoaderService;
    private final TopCategoryDao topCategoryDao;

    @Inject
    public CategoryAddController(FxmlLoaderService loaderService, TopCategoryDao topCategoryDao){
        this.topCategoryDao = topCategoryDao;
        this.fxmlLoaderService = loaderService;
    }

    @FXML
    private void initialize() {
        addTopButton.setOnAction(this::onTopCategoryAddAction);

        confirmButton.addEventHandler(ActionEvent.ACTION, e -> {
            if(!name.getText().isEmpty()) {
                updateModel();
                approved = true;
                stage.close();
            }
        });

        refreshList();
    }

    private void onTopCategoryAddAction(ActionEvent event) {
        TopCategory topCategory = new TopCategory();

        Stage stage = new Stage();

        FXMLLoader loader = fxmlLoaderService.getLoader(getClass().getResource("/view/dialog/TopCategoryAdd.fxml"));
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root, 400 ,200));
            TopCategoryAddController controller = loader.getController();
            controller.setStage(stage);
            controller.setModel(topCategory);

            stage.setTitle("Add new top category");
            stage.showAndWait();

            if(controller.isApproved()) {
                try {
                    topCategoryDao.save(topCategory);
                    refreshList();
                    topCategorySelection.getSelectionModel().select(topCategory);
                } catch (PersistenceException e) {
                    e.printStackTrace();
                }
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setModel(Category category){
        this.category=category;
        updateControls();
    }

    private void updateControls() {
        name.setText(category.getName());
        TopCategory topCategory = category.getTopCategory();
        if(topCategory!=null){
            topCategorySelection.getSelectionModel().select(topCategory);
        }
    }

    private void refreshList() {
        topCategorySelection.setItems(FXCollections.observableList(topCategoryDao.findAll()));
    }

    private void updateModel() {
        TopCategory topCategory = topCategorySelection.getValue();
        category.setTopCategory(topCategory);
        category.setName(name.getText());
    }
}
