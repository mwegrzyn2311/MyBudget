package controller.dialog;

import controller.CategoriesViewController;
import dao.CategoryDao;
import dao.TopCategoryDao;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
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
    Button addButton;
    @FXML
    TreeView<String> topCategoryPicker;
    @FXML
    TextField name;

    FxmlLoaderService fxmlLoaderService;
    private TopCategoryDao topCategoryDao;
    private CategoryDao categoryDao;

    private final TreeItem<String> root = new TreeItem<>("Top categories");

    @Inject
    public CategoryAddController(FxmlLoaderService loaderService, TopCategoryDao topCategoryDao, CategoryDao categoryDao){
        this.topCategoryDao = topCategoryDao;
        this.categoryDao = categoryDao;
        this.fxmlLoaderService = loaderService;
    }

    @FXML
    private void initialize() {
        addTopButton.setOnAction(this::onTopCategoryAddAction);

        addButton.addEventHandler(ActionEvent.ACTION, e -> {
            if(! name.getText().isEmpty()) {
                addCategory();
                approved = true;
                stage.close();
            }
        });
        root.setExpanded(false);
        topCategoryPicker.setShowRoot(false);
        CategoriesViewController.createTopTreeView(topCategoryDao, root);
        this.topCategoryPicker.setRoot(root);

        this.topCategoryPicker.getSelectionModel().selectionModeProperty().set(SelectionMode.SINGLE);

        this.topCategoryPicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isLeaf()) {
                Platform.runLater(() -> topCategoryPicker.getSelectionModel().clearSelection());
            }
        });

        
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
                    topCategoryDao.refresh(topCategory);
                    refreshList();
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
            topCategoryPicker.getSelectionModel().select(topCategoryPicker
                    .getRow(getTreeViewItem(root,topCategory.getName())));
        }
    }

    private void refreshList() {
        this.root.getChildren().clear();
        CategoriesViewController.createTopTreeView(topCategoryDao, root);
        this.topCategoryPicker.setRoot(root);
    }

    private void addCategory() {

        TopCategory topCategory = topCategoryDao.findByName(this.topCategoryPicker.getSelectionModel().getSelectedItem()
                .getValue()).get();

        category.setTopCategory(topCategory);

        category.setName(name.getText());
    }
}
