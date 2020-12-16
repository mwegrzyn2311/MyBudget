package helper;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import model.BaseCategory;
import model.Category;
import model.TopCategory;

import java.util.List;

public class CategoryTreeListHelper {
    public static TreeItem<BaseCategory> createTreeView(List<TopCategory> topCategoryList, boolean showChildless) {
        TreeItem<BaseCategory> root= new TreeItem<>();
        for(TopCategory topCategory: topCategoryList){
            if(showChildless || topCategory.getChildCategories().size() > 0) {
                TreeItem<BaseCategory> topCategoryTree = new TreeItem<>(topCategory);
                root.getChildren().add(topCategoryTree);
                for (Category category : topCategory.getChildCategories()) {
                    topCategoryTree.getChildren().add(new TreeItem<>(category));
                }
            }
        }
        return root;
    }

    public static TreeCell<BaseCategory> buildTreeCell(TreeView<BaseCategory> view) {
        return new TreeCell<>() {
            @Override
            protected void updateItem(BaseCategory item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : getItem().getName());
                setGraphic(null);

            }
        };
    }
}
