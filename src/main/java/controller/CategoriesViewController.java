package controller;

import controller.dialog.CategoryAddController;
import controller.dialog.OperationEditController;
import controller.dialog.TopCategoryAddController;
import dao.CategoryDao;
import dao.TopCategoryDao;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Category;
import model.Operation;
import model.TopCategory;
import service.FxmlLoaderService;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class CategoriesViewController extends TabController {

    private final TopCategoryDao topCategoryDao;
    private final FxmlLoaderService fxmlLoaderService;
    private final CategoryDao categoryDao;

    @FXML
    private Button editCategory;

    @FXML
    private Button deleteCategory;

    @FXML
    private Button addCategory;

    @FXML
    private TreeView<String> categoriesView;

    private TreeItem<String> root = new TreeItem<>("Categories");


    @Inject
    public CategoriesViewController(TopCategoryDao topCategoryDao, FxmlLoaderService loaderService,
                                    CategoryDao categoryDao){
        this.topCategoryDao = topCategoryDao;
        this.fxmlLoaderService=loaderService;
        this.categoryDao=categoryDao;
    }


    @FXML
    public void initialize(){
        addCategory.setOnAction(this::addCategory);
        editCategory.setOnAction(this::editCategory);
        deleteCategory.setOnAction(this::deleteCategory);
        root.setExpanded(true);
        createTreeView(topCategoryDao, root);
        this.categoriesView.setRoot(root);
        this.categoriesView.setShowRoot(false);
        deleteCategory.disableProperty().bind(Bindings.isEmpty(categoriesView.getSelectionModel().getSelectedItems()));
        editCategory.disableProperty().bind(Bindings.size(categoriesView.getSelectionModel().getSelectedItems())
                .isNotEqualTo(1));
        this.categoriesView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void deleteCategory(ActionEvent actionEvent) {
        if(categoriesView.getSelectionModel().getSelectedItem().isLeaf()){
            Category category = categoryDao.findByName(categoriesView.getSelectionModel().getSelectedItem()
                    .getValue()).get();
            this.categoryDao.delete(category);
        }
        else{
            TopCategory topCategory = topCategoryDao.findByName(categoriesView.getSelectionModel().getSelectedItem()
                    .getValue()).get();
            ObservableList<TreeItem<String>> childCategories=categoriesView.getSelectionModel().getSelectedItem()
                    .getChildren();
            for(TreeItem<String> child: childCategories){
                Category category = categoryDao.findByName(child.getValue()).get();
                this.categoryDao.delete(category);
            }
            this.topCategoryDao.delete(topCategory);
        }
        refreshList();
    }

    private void editCategory(ActionEvent actionEvent) {
        if(categoriesView.getSelectionModel().getSelectedItem().isLeaf()){
            Category category = categoryDao.findByName(categoriesView.getSelectionModel().getSelectedItem()
                    .getValue()).get();

            Stage stage = new Stage();

            FXMLLoader loader = fxmlLoaderService.getLoader(getClass().getResource("/view/dialog/CategoryAdd.fxml"));
            try {
                Parent root = loader.load();
                stage.setScene(new Scene(root, 600 ,400));
                CategoryAddController controller = loader.getController();
                controller.setStage(stage);
                controller.setModel(category);

                stage.setTitle("Edit category");
                stage.showAndWait();

                if(controller.isApproved()) {
                    try {
                        categoryDao.update(category);
                        this.topCategoryDao.refresh(category.getTopCategory());
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                    }
                }
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        else{
            TopCategory topCategory= topCategoryDao.findByName(categoriesView.getSelectionModel().getSelectedItem()
                    .getValue()).get();

            Stage stage = new Stage();

            FXMLLoader loader = fxmlLoaderService.getLoader(getClass().getResource("/view/dialog/TopCategoryAdd.fxml"));
            try {
                Parent root = loader.load();
                stage.setScene(new Scene(root, 400 ,200));
                TopCategoryAddController controller = loader.getController();
                controller.setStage(stage);
                controller.setModel(topCategory);

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
            CategoryAddController controller = loader.getController();
            controller.setStage(stage);
            controller.setModel(category);

            stage.setTitle("Add new category");
            stage.showAndWait();

            if(controller.isApproved()) {
                try {
                    categoryDao.save(category);
                    this.topCategoryDao.refresh(category.getTopCategory());
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
        createTreeView(topCategoryDao, root);
        this.categoriesView.setRoot(root);
        categoriesView.refresh();
    }

    public static void createTreeView(TopCategoryDao topCategoryDao, TreeItem<String> root) {
        List<TopCategory> categories = topCategoryDao.findAll();
        for(TopCategory topCategory: categories){
            TreeItem<String> topCategoryTree = new TreeItem<>(topCategory.getName());
            root.getChildren().add(topCategoryTree);
            for(Category category: topCategory.getChildCategories()){
                topCategoryTree.getChildren().add(new TreeItem<>(category.getName()));
            }
        }
    }

    public static void createTopTreeView(TopCategoryDao topCategoryDao, TreeItem<String> root) {
        List<TopCategory> categories = topCategoryDao.findAll();
        for(TopCategory topCategory: categories){
            TreeItem<String> topCategoryTree = new TreeItem<>(topCategory.getName());
            root.getChildren().add(topCategoryTree);
        }
    }
}
