package helper;

import dao.CategoryDao;
import dao.TopCategoryDao;
import model.Category;
import model.OperationType;
import model.TopCategory;

import java.util.Arrays;

public class DBInitHelper {
    private static final String[] topCategories = {"Incomes", "Utility bills", "Food", "Shopping", "Transport", "Entertainment"};
    private static final OperationType[] categoryTypes = {
            OperationType.Income,
            OperationType.Expense,
            OperationType.Expense,
            OperationType.Expense,
            OperationType.Expense,
            OperationType.Expense
    };
    private static final String[][] categories = {
            {"Salary", "Odd jobs", "Pension", "Personal savings"},
            {"Rent", "Gas", "Electricity", "Water", "Garbage"},
            {"Restaurant", "Bar"},
            {"Groceries", "Clothes", "Shoes", "Presents"},
            {"Car expenses", "Fuel", "Insurance"},
            {"Books", "Cinema"}
    };
    public static void initializeDB(TopCategoryDao topCategoryDao, CategoryDao categoryDao) {
        for(int i = 0; i < topCategories.length; ++i)
        {
            TopCategory topCategory = new TopCategory(topCategories[i], categoryTypes[i]);
            Arrays.stream(categories[i]).forEach(s -> topCategory.addChildCategory(new Category(s, topCategory)));
            topCategoryDao.save(topCategory);
        }
    }
}
