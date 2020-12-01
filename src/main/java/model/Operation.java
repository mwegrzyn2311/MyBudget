package model;

import javax.persistence.*;

@Entity
@Table(name = "Operation")
public class Operation {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    public Operation() {}

    public Operation(int id) {
        this.id = id;
    }
}
