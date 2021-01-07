package model;

import javax.persistence.*;

@Entity
@Access(AccessType.PROPERTY)
@Table(name = "Category")
public class Category extends BaseCategory {
    private TopCategory topCategory;

    public Category(String name, TopCategory topCategory){
        this.name = name;
        this.topCategory = topCategory;
    }

    public Category() {}

    @ManyToOne
    public TopCategory getTopCategory(){return this.topCategory;}
    public void setTopCategory(TopCategory topCategory) {this.topCategory = topCategory;}
}
