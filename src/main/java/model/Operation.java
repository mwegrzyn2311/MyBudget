package model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Access(AccessType.PROPERTY)
@Table(name = "Operation")
public class Operation {
    private Long id;

    private ObjectProperty<Account> account;

    private ObjectProperty<BigDecimal> amount;

    private ObjectProperty<LocalDate> date;

    private StringProperty comment;

    private ObjectProperty<Category> category;


    public Operation() {
        this.account = new SimpleObjectProperty<>();
        this.amount = new SimpleObjectProperty<>();
        this.date = new SimpleObjectProperty<>();
        this.comment = new SimpleStringProperty();
        this.category = new SimpleObjectProperty<>();
    }

    public Operation(Account account, BigDecimal amount, LocalDate date, String comment, Category category) {
        this.account = new SimpleObjectProperty<>(account);
        this.amount = new SimpleObjectProperty<>(amount);
        this.date = new SimpleObjectProperty<>(date);
        this.comment = new SimpleStringProperty(comment);
        this.category = new SimpleObjectProperty<>(category);
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

    @ManyToOne
    public Account getAccount() {
        return account.getValue();
    }
    public void setAccount(Account account) {
        this.account = new SimpleObjectProperty<>(account);
    }
    public ObjectProperty<Account> accountProperty() {
        return account;
    }

    @Column(name = "amount")
    public BigDecimal getAmount() {
        return amount.getValue();
    }
    public void setAmount(BigDecimal amount) {
        this.amount = new SimpleObjectProperty<>(amount);
    }
    public ObjectProperty<BigDecimal> amountProperty() {
        return amount;
    }

    @Column(name = "date")
    public LocalDate getDate() {
        return date.getValue();
    }
    public void setDate(LocalDate date) {
        this.date = new SimpleObjectProperty<>(date);
    }
    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    @Column(name = "comment")
    public String getComment() {
        return comment.getValue();
    }
    public void setComment(String comment) {
        this.comment = new SimpleStringProperty(comment);
    }
    public StringProperty commentProperty() {
        return comment;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="category_fk")
    public Category getCategory() {return this.category.getValue();}
    public void setCategory(Category category) {this.category=new SimpleObjectProperty<>(category);}
    public ObjectProperty<Category> categoryProperty() {return this.category;}
}
