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
    private ObjectProperty<Date> lastDay;
    private List<CategoryBudget> categoryBudgets;

    public MonthlyBudget() {
        this.name = new SimpleStringProperty();
        this.firstDay = new SimpleObjectProperty<>();
        this.lastDay = new SimpleObjectProperty<>();
        this.categoryBudgets = new LinkedList<>();
    }

    public MonthlyBudget(String name, Date firstDay, List<CategoryBudget> categoryBudgets) {
        this.name = new SimpleStringProperty(name);
        this.firstDay = new SimpleObjectProperty<>(firstDay);
        this.lastDay = new SimpleObjectProperty<>(calculateLastDay());
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
        this.lastDay.set(calculateLastDay());
    }
    public ObjectProperty<Date> firstDayProperty() {
        return this.firstDay;
    }

    @Column(name = "lastDay")
    public Date getLastDay() {
        return this.lastDay.getValue();
    }
    public void setLastDay(Date lastDay) {
        this.lastDay.set(lastDay);
    }
    public ObjectProperty<Date> lastDayProperty() {
        return this.lastDay;
    }

    @Column(name = "name")
    public String getName() {
        return this.name.getValue();
    }
    public void setName(String name) {
        this.name.set(name);
    }
    public StringProperty nameProperty() {
        return this.name;
    }


    @OneToMany(mappedBy = "monthlyBudget", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, orphanRemoval = true)
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


    private Date calculateLastDay() {
        Calendar c = Calendar.getInstance();
        c.setTime(firstDay.getValue());
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DAY_OF_MONTH, -1);
        return c.getTime();
    }
    public BigDecimal initialBalance() {
        BigDecimal expenses = categoryBudgets.stream().filter(categoryBudget ->
             categoryBudget.getCategory().getTopCategory().getOperationType().compareTo(OperationType.Expense)==0
        ).map(CategoryBudget::getInitialBudget)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal incomes = categoryBudgets.stream().filter(categoryBudget ->
            categoryBudget.getCategory().getTopCategory().getOperationType().compareTo(OperationType.Income)==0
        ).map(CategoryBudget::getInitialBudget)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return incomes.subtract(expenses);
    }
    public BigDecimal currentBalance() {
        return categoryBudgets.stream()
                .map(CategoryBudget::getCurrentBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    @Transient
    public boolean isPreserved() {
        for(CategoryBudget cb : categoryBudgets) {
            if(cb.getCurrentBalance().compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }
        }
        return true;
    }
}
