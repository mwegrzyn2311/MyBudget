package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import model.Category;
import model.TopCategory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.Optional;

public class TopCategoryDao extends GenericDao<TopCategory> {

    @Inject
    public TopCategoryDao(Provider<EntityManager> entityManager) {
        super(entityManager, TopCategory.class);
    }

    public Optional<TopCategory> findByName(final String name) {
        try {
            TopCategory category = entityManager.get().createQuery("SELECT c FROM TopCategory c WHERE c.name = :name",
                    TopCategory.class)
                    .setParameter("name", name).getSingleResult();
            return Optional.of(category);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void delete(TopCategory entity) {
        entityManager.get().createQuery("DELETE FROM CategoryBudget cb WHERE cb.category " +
                "IN (SELECT c FROM Category c where c.topCategory = :entity)")
                .setParameter("entity", entity).executeUpdate();
        entityManager.get().createQuery("DELETE FROM Operation o WHERE o.category " +
                "IN (SELECT c FROM Category c where c.topCategory = :entity)")
                .setParameter("entity", entity).executeUpdate();
        entityManager.get().remove(entity);
    }

    public void clear(){
        entityManager.get().clear();
    }
}
