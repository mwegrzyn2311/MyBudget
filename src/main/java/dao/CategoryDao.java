package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import model.Account;
import model.Category;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
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
}
