package controller;

import controller.dialog.OperationEditController;
import dao.TopCategoryDao;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.Category;
import model.TopCategory;

import javax.inject.Inject;
import java.util.List;

public class CategoriesViewController extends TabController {

    private final TopCategoryDao topCategoryDao;

    @FXML
    private TreeView<String> categoriesView;

    private final TreeItem<String> root = new TreeItem<>("Categories");

    @Inject
    public CategoriesViewController(TopCategoryDao topCategoryDao){
        this.topCategoryDao = topCategoryDao;
    }

    @FXML
    public void initialize(){
        root.setExpanded(true);
        createTreeView(topCategoryDao, root);
        this.categoriesView.setRoot(root);
    }

    @Override
    public void onSelected() { }

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
}
