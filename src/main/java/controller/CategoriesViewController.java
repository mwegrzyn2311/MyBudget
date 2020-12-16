package controller;

import controller.dialog.CategoryEditController;
import controller.dialog.TopCategoryAddController;
import dao.TopCategoryDao;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.BaseCategory;
import model.Category;
import model.TopCategory;
import service.FxmlLoaderService;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.io.IOException;

public class CategoriesViewController extends TabController {

    private final TopCategoryDao topCategoryDao;
    private final FxmlLoaderService fxmlLoaderService;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button addButton;

    @FXML
    private TreeView<BaseCategory> categoriesView;

    private TreeItem<String> root = new TreeItem<>("Categories");


    @Inject
    public CategoriesViewController(TopCategoryDao topCategoryDao, FxmlLoaderService loaderService){
        this.topCategoryDao = topCategoryDao;
        this.fxmlLoaderService=loaderService;
    }


    @FXML
    public void initialize(){
        addButton.setOnAction(this::addCategory);
        editButton.setOnAction(this::editCategory);
        deleteButton.setOnAction(this::deleteCategory);
        refreshList();
        categoriesView.setShowRoot(false);
        categoriesView.setCellFactory(CategoryTreeListHelper::buildTreeCell);

        deleteButton.disableProperty().bind(Bindings.isEmpty(categoriesView.getSelectionModel().getSelectedItems()));
        editButton.disableProperty().bind(Bindings.size(categoriesView.getSelectionModel().getSelectedItems())
                .isNotEqualTo(1));
        this.categoriesView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void deleteCategory(ActionEvent actionEvent) {
        TreeItem<BaseCategory> selected = categoriesView.getSelectionModel().getSelectedItem();
        if(selected.getValue() instanceof Category){
            final Category category = (Category) selected.getValue();
            final TopCategory topCategory = category.getTopCategory();
            topCategory.removeChildCategory(category);
            topCategoryDao.update(topCategory);
        } else {
            TopCategory topCategory = (TopCategory) selected.getValue();
            this.topCategoryDao.delete(topCategory);
        }
        refreshList();
    }

    private void editCategory(ActionEvent actionEvent) {
        TreeItem<BaseCategory> selected = categoriesView.getSelectionModel().getSelectedItem();
        if(selected.getValue() instanceof Category){
            Category category = (Category) selected.getValue();

            Stage stage = new Stage();

            FXMLLoader loader = fxmlLoaderService.getLoader(getClass().getResource("/view/dialog/CategoryAdd.fxml"));
            try {
                Parent root = loader.load();
                stage.setScene(new Scene(root, 600 ,400));
                CategoryEditController controller = loader.getController();
                controller.setStage(stage);
                controller.setModel(category);
                controller.setConfirmButtonText("Edit");

                stage.setTitle("Edit category");
                stage.showAndWait();

                if(controller.isApproved()) {
                    try {
                        topCategoryDao.update((TopCategory) selected.getParent().getValue());
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                    }
                }
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        else{
            TopCategory topCategory= (TopCategory) selected.getValue();

            Stage stage = new Stage();

            FXMLLoader loader = fxmlLoaderService.getLoader(getClass().getResource("/view/dialog/TopCategoryAdd.fxml"));
            try {
                Parent root = loader.load();
                stage.setScene(new Scene(root, 400 ,200));
                TopCategoryAddController controller = loader.getController();
                controller.setStage(stage);
                controller.setModel(topCategory);
                controller.setConfirmButtonText("Edit");

                stage.setTitle("Edit top category");
                stage.showAndWait();

                if(controller.isApproved()) {
                    try {
                        topCategoryDao.update(topCategory);
                        this.topCategoryDao.refresh(topCategory);
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                    }
                }
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        refreshList();
    }

    private void addCategory(ActionEvent event) {
        Category category = new Category();

        Stage stage = new Stage();

        FXMLLoader loader = fxmlLoaderService.getLoader(getClass().getResource("/view/dialog/CategoryAdd.fxml"));
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root, 600 ,400));
            CategoryEditController controller = loader.getController();
            controller.setStage(stage);
            controller.setModel(category);
            controller.setConfirmButtonText("Create");

            stage.setTitle("Add new category");
            stage.showAndWait();

            if(controller.isApproved()) {
                try {
                    final TopCategory topCategory = category.getTopCategory();
                    topCategory.addChildCategory(category);
                    topCategoryDao.save(topCategory);
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
    public void onSelected() { refreshList();}

    private void refreshList(){
        this.root.getChildren().clear();
        categoriesView.setRoot(CategoryTreeListHelper.createTreeView(topCategoryDao.findAll(), true));
        categoriesView.refresh();
    }
}
