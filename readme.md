## MyBudget

Desktop app for managing home budget made in Java for Objective Technologies academic lessons

### Creators

- [Anna Zając](https://github.com/Iffern)

- [Michał Jakubek](https://github.com/XertDev)
- [Michał Węgrzyn](https://github.com/mwegrzyn2311)

### Tech stack

- Java 14
- Gradle
- JavaFX
- Hibernate
- Guice
- ContorlsFX library

### App preview

##### Budgets List - default view

<img src="./readme_images/Budgets.jpg?raw=true" style="zoom:70%"/>

App starts in Account list view where users can see and manage all their accounts. By double-clicking on the account row an account tab is opened.

##### Budget view

<img src="./readme_images/ExampleBudget.jpg?raw=true" style="zoom:70%"/>

In budget view users can see all operations for the chosen account, they can add new operations or manage (edit/delete) existing ones.

##### Monthly budget list

<img src="./readme_images/MonthlyBudgets.jpg?raw=true" style="zoom:70%"/>

Another feature are Monthly Budgets. Users can decide to set their spendings and incomes goals for any month and in this view they can see overview of each of the Balances and again - manage them

##### Monthly budget

<img src="./readme_images/ExampleMonthlyBudget.jpg?raw=true" style="zoom:70%"/>

After choosing one of the Monthly Budgets, users are welcomed with this view where they can manage their planned balances for each category they decide to set goals for. They can also edit or delete already existing category balances.

##### Statistics

<img src="./readme_images/IncomeOutcomeStats.jpg?raw=true" style="zoom:70%"/>

<img src="./readme_images/OutcomePerCatStats.jpg?raw=true" style="zoom:70%"/>

The last presented views are statistics views - one for incomes and outcomes per year or per month and the second one showing outcomes per categories per chosen month and year.

##### Add/Edit dialogs

<img src="./readme_images/ExampleEditDialog.jpg?raw=true" style="zoom:70%"/>

When adding new element(account, operation, monthly budget, etc.) or editing an existing one, a dialog window will show up which users can use to fill in required info and confirm to create/edit element. Data validation is performed with help of ControlsFX library.

##### Additional info

App view are managed using tab system for users convenience.