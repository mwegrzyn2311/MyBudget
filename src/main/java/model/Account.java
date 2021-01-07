package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "Account")
@Access(AccessType.PROPERTY)
public class Account {

    private Long id;

    private StringProperty name;

    private StringProperty accountNumber;

    private ObjectProperty<BigDecimal> initialBalance;

    private List<Operation> operations;

    private ObservableList<Operation> obsOperations;

    public Account() {
        this.name = new SimpleStringProperty();
        this.accountNumber = new SimpleStringProperty();
        this.initialBalance = new SimpleObjectProperty<>();
        this.operations = new LinkedList<>();
        this.obsOperations = FXCollections.observableList(new LinkedList<>());
    }

    public Account(String name, String accountNumber, BigDecimal initialBalance, List<Operation> operations) {
        this.name = new SimpleStringProperty(name);
        this.accountNumber = new SimpleStringProperty(accountNumber);
        this.initialBalance = new SimpleObjectProperty<>(initialBalance);
        this.operations = operations;
        this.operations = FXCollections.observableList(operations);
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


    @Column(name = "name")
    public String getName() {
        return name.get();
    }
    public void setName(String name) {
        this.name = new SimpleStringProperty(name);
    }
    public StringProperty nameProperty() {
        return name;
    }

    @Column(name = "accountNumber")
    public String getAccountNumber() {
        return accountNumber.get();
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = new SimpleStringProperty(accountNumber);
    }
    public StringProperty accountNumberProperty() {
        return accountNumber;
    }

    @Column(name = "initialBalance")
    public BigDecimal getInitialBalance() {
        return initialBalance.getValue();
    }
    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = new SimpleObjectProperty<>(initialBalance);
    }
    public ObjectProperty<BigDecimal> initialBalanceProperty() {
        return initialBalance;
    }

    @OneToMany(mappedBy = "account", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, orphanRemoval = true)
    public List<Operation> getOperations() {
        return operations;
    }
    public void setOperations(List<Operation> operations) {
        this.operations = operations;
        this.obsOperations = FXCollections.observableList(this.operations);
    }
    public ObservableList<Operation> operationsObservableList() {
        return this.obsOperations;
    }
    public void addOperation(Operation operation) {
        obsOperations.add(operation);
    }
    public void removeOperation(Operation operation) {
        obsOperations.remove(operation);
    }
}
