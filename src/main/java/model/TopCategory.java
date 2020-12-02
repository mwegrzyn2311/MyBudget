package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
@Access(AccessType.PROPERTY)
@Table(name = "TopCategory")
public class TopCategory implements Serializable {
    private Long id;
    private String name;
    private List<Category> childCategories;
    private OperationType operationType;

    public TopCategory(String name, OperationType operationType){
        this.name = name;
        this.childCategories = new ArrayList<>();
        this.operationType = operationType;
    }

    public TopCategory() { }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="name")
    public String getName(){return this.name;}
    public void setName(String name){this.name=name;}

    @Column(name="operationType")
    public OperationType getOperationType() {return this.operationType;}
    public void setOperationType(OperationType operationType) {this.operationType = operationType;}

    @OneToMany(mappedBy = "topCategory")
    public List<Category> getChildCategories() {return this.childCategories;}
    public void setChildCategories(List<Category> childCategories) {this.childCategories=childCategories;}
    public void addChildCategory(Category childCategory) {this.childCategories.add(childCategory);}
}
