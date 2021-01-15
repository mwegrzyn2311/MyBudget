package model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Access(AccessType.PROPERTY)
@Table(name = "CategoryBudget")
public class CategoryBudget {
    private Long id;
    private ObjectProperty<Category> category;
    private ObjectProperty<BigDecimal> initialBudget;
    private ObjectProperty<MonthlyBudget> monthlyBudget;

    @Formula("(select coalesce(sum(op.amount), 0) from Operation op join MonthlyBudget mb " +
            "on mb.id = monthlyBudget_id and op.category_fk = category_fk and op.date >= mb.firstDay and op.date <= mb.lastDay)")
    @Access(AccessType.FIELD)
    private BigDecimal currentSpending;

    public CategoryBudget() {
        category = new SimpleObjectProperty<>();
        initialBudget = new SimpleObjectProperty<>();
        monthlyBudget = new SimpleObjectProperty<>();
    }

    public CategoryBudget(Category category, BigDecimal initialBudget, MonthlyBudget monthlyBudget) {
        this.category = new SimpleObjectProperty<>(category);
        this.initialBudget = new SimpleObjectProperty<>(initialBudget);
        this.monthlyBudget = new SimpleObjectProperty<>(monthlyBudget);
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
    @OnDelete(action = OnDeleteAction.CASCADE)
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

    @ManyToOne
    public MonthlyBudget getMonthlyBudget() {
        return monthlyBudget.getValue();
    }
    public void setMonthlyBudget(MonthlyBudget monthlyBudget) {
        this.monthlyBudget = new SimpleObjectProperty<>(monthlyBudget);
    }
    public ObjectProperty<MonthlyBudget> monthlyBudgetProperty() {
        return monthlyBudget;
    }

    @Transient
    public BigDecimal getCurrentBalance() {
        if(this.getCategory().getTopCategory().getOperationType().compareTo(OperationType.Income)==0){
            return this.getInitialBudget().negate().add(this.currentSpending);
        }
        else {
            return this.getInitialBudget().add(this.currentSpending);
        }
    }
}
