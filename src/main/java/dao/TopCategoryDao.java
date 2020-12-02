package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import model.TopCategory;

import javax.persistence.EntityManager;

public class TopCategoryDao extends GenericDao<TopCategory> {

    @Inject
    public TopCategoryDao(Provider<EntityManager> entityManager) {
        super(entityManager, TopCategory.class);
    }
}
