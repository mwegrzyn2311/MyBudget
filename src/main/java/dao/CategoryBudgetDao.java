package dao;

import com.google.inject.Provider;
import model.CategoryBudget;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class CategoryBudgetDao extends GenericDao<CategoryBudget> {
    @Inject
    public CategoryBudgetDao(Provider<EntityManager> entityManager) {
        super(entityManager, CategoryBudget.class);
    }
}
