package model;

import java.util.ArrayList;
import java.util.List;

public class TopCategory {
    private String name;
    private List<Category> childCategories;

    public TopCategory(String name){
        this.name = name;
        this.childCategories = new ArrayList<>();
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
