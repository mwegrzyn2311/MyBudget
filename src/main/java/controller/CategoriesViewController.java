package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.Category;
import model.TopCategory;
import service.JSONLoaderService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CategoriesViewController implements Initializable {

    @FXML
    private TreeView<String> categoriesView;

    private final JSONLoaderService jsonLoaderService;
    private final TreeItem<String> root = new TreeItem<>("Categories");

    public CategoriesViewController(){
        this.jsonLoaderService = JSONLoaderService.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        root.setExpanded(true);
        List<TopCategory> categories = this.jsonLoaderService.getCategories();
        for(TopCategory topCategory: categories){
            TreeItem<String> topCategoryTree = new TreeItem<>(topCategory.getName());
            root.getChildren().add(topCategoryTree);
            for(Category category: topCategory.getChildCategories()){
                topCategoryTree.getChildren().add(new TreeItem<>(category.getName()));
            }
        }
        this.categoriesView.setRoot(root);
    }
}
