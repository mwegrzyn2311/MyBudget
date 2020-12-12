package model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
@Access(AccessType.PROPERTY)
@Table(name = "TopCategory")
public class TopCategory extends BaseCategory{

    private List<Category> childCategories;
    private OperationType operationType;

    public TopCategory(String name, OperationType operationType){
        this.name = name;
        this.childCategories = new ArrayList<>();
        this.operationType = operationType;
    }

    public TopCategory() {
        childCategories = new LinkedList<>();
    }

    @Column(name="operationType")
    public OperationType getOperationType() {return this.operationType;}
    public void setOperationType(OperationType operationType) {this.operationType = operationType;}

    @OneToMany(mappedBy = "topCategory", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, orphanRemoval = true)
    public List<Category> getChildCategories() {return this.childCategories;}
    public void setChildCategories(List<Category> childCategories) {this.childCategories=childCategories;}
    public void addChildCategory(Category childCategory) {this.childCategories.add(childCategory);}
    public void removeChildCategory(Category childCategory) {
        childCategories.remove(childCategory);
    }

    @Override
    public String toString() {
        return name;
    }
}
