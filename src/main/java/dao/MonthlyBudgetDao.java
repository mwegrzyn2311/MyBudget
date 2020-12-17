package dao;

import com.google.inject.Provider;
import model.MonthlyBudget;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class MonthlyBudgetDao extends GenericDao<MonthlyBudget> {
    @Inject
    public MonthlyBudgetDao(Provider<EntityManager> entityManager) {
        super(entityManager, MonthlyBudget.class);
    }
}
