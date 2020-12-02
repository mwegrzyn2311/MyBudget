package model;

import java.util.ArrayList;
import java.util.List;

public class TopCategory {
    private String name;
    private List<Category> childCategories;
    private OperationType operationType;

    public TopCategory(String name, OperationType operationType){
        this.name = name;
        this.childCategories = new ArrayList<>();
        this.operationType = operationType;
    }

    public List<Category> getChildCategories() {
        return childCategories;
    }

    public String getName() {
        return name;
    }

    public void addChildCategory(Category category){
        this.childCategories.add(category);
    }
}
