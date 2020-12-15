package model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Access(AccessType.PROPERTY)
@Table(name = "CategoryBudget")
public class CategoryBudget {
    private Long id;
    private ObjectProperty<Category> category;
    private ObjectProperty<BigDecimal> initialBudget;

    @Formula("(select coalesce(sum(op.amount), 0) from Operation op where op.category_fk = category_fk)")
    @Access(AccessType.FIELD)
    private BigDecimal currentSpending;

    public CategoryBudget() {
        category = new SimpleObjectProperty<>();
        initialBudget = new SimpleObjectProperty<>();
    }

    public CategoryBudget(Category category, BigDecimal initialBudget) {
        this.category = new SimpleObjectProperty<>(category);
        this.initialBudget = new SimpleObjectProperty<>(initialBudget);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_fk")
    public Category getCategory() {
        return category.getValue();
    }

    public void setCategory(Category category) {
        this.category.set(category);
    }

    public ObjectProperty<Category> categoryProperty() {
        return this.category;
    }

    @Column(name = "initialBudget")
    public BigDecimal getInitialBudget() {
        return initialBudget.getValue();
    }

    public void setInitialBudget(BigDecimal initialBudget) {
        this.initialBudget.set(initialBudget);
    }

    public ObjectProperty<BigDecimal> initialBudgetProperty() {
        return this.initialBudget;
    }

    // TODO: Consider moving it to another place
    public BigDecimal currentBalance() {
        if(this.getCategory().getTopCategory().getOperationType().compareTo(OperationType.Income)==0){
            return this.getInitialBudget().negate().add(this.currentSpending);
        }
        else {
            return this.getInitialBudget().add(this.currentSpending);
        }
    }
}
