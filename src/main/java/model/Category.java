package model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Access(AccessType.PROPERTY)
@Table(name = "Category")
public class Category implements Serializable {
    private Long id;
    private String name;
    private TopCategory topCategory;

    public Category(String name, TopCategory topCategory){
        this.name = name;
        this.topCategory = topCategory;
    }

    public Category() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    public TopCategory getTopCategory(){return this.topCategory;}
    public void setTopCategory(TopCategory topCategory) {this.topCategory = topCategory;}

    @Column(name="name")
    public String getName(){return this.name;}
    public void setName(String name){this.name=name;}
}
