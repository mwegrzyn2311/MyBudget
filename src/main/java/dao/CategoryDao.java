package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import model.Category;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.Optional;

public class CategoryDao extends GenericDao<Category>{
    @Inject
    public CategoryDao(Provider<EntityManager> entityManager) {
        super(entityManager, Category.class);
    }

    public Optional<Category> findByName(final String name) {
        try {
            Category category = entityManager.get().createQuery("SELECT c FROM Category c WHERE c.name = :name",
                    Category.class)
                    .setParameter("name", name).getSingleResult();
            return Optional.of(category);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void delete(Category entity) {
        EntityManager em = entityManager.get();
        Query deleteOperations = em.createNativeQuery("DELETE from Operation where category_fk = :category_id")
                .setParameter("category_id", entity.getId());
        Query deleteCategoryBudget = em.createNativeQuery("DELETE from CategoryBudget where category_fk = :category_id")
                .setParameter("category_id", entity.getId());
        deleteOperations.executeUpdate();
        deleteCategoryBudget.executeUpdate();
        em.remove(entity);
        em.clear();
    }
}
