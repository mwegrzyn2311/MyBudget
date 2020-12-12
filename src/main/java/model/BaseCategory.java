package model;

import javax.persistence.*;

@MappedSuperclass
public class BaseCategory {
    protected Long id;
    protected String name;

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
}
