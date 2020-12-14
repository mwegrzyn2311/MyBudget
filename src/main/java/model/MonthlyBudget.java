package model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "MonthlyBudget")
@Access(AccessType.PROPERTY)
public class MonthlyBudget {
    private Long id;
    private StringProperty name;
    private ObjectProperty<Date> firstDay;
    private List<CategoryBudget> categoryBudgets;

    public MonthlyBudget() {
        this.name = new SimpleStringProperty();
        this.firstDay = new SimpleObjectProperty<>();
        this.categoryBudgets = new LinkedList<>();
    }

    public MonthlyBudget(String name, Date firstDay, List<CategoryBudget> categoryBudgets) {
        this.name = new SimpleStringProperty(name);
        this.firstDay = new SimpleObjectProperty<>(firstDay);
        this.categoryBudgets = categoryBudgets;
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

    @Column(name = "firstDay")
    public Date getFirstDay() {
        return this.firstDay.getValue();
    }
    public void setFirstDay(Date firstDay) {
        this.firstDay.set(firstDay);
    }
    public ObjectProperty<Date> firstDayProperty() {
        return this.firstDay;
    }

    @Column(name = "name")
    public String getName() { return this.name.getValue(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return this.name; }

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, orphanRemoval = true)
    public List<CategoryBudget> getCategoryBudgets() {
        return categoryBudgets;
    }
    public void setCategoryBudgets(List<CategoryBudget> categoryBudgets) {
        this.categoryBudgets = categoryBudgets;
    }
    public ObservableList<CategoryBudget> categoryBudgetsObservableList() {
        return FXCollections.observableList(this.categoryBudgets);
    }
    public void addCategoryBudget(CategoryBudget cb) {
        categoryBudgets.add(cb);
    }
    public void removeCategoryBudget(CategoryBudget cb) {
        categoryBudgets.remove(cb);
    }


    // TODO: Consider placing it in another place
    public Date lastDay() {
        Calendar c = Calendar.getInstance();
        c.setTime(firstDay.getValue());
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DAY_OF_MONTH, -1);
        return c.getTime();
    }
    public BigDecimal initialBalance() {
        return categoryBudgets.stream()
                .map(CategoryBudget::getInitialBudget)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal currentBalance() {
        return categoryBudgets.stream()
                .map(CategoryBudget::currentBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
