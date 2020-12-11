package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import model.Category;
import model.TopCategory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;
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

    public List<TopCategory> findAll(){
        List <TopCategory> topCategories =
                entityManager.get().createQuery("SELECT c FROM TopCategory c", TopCategory.class).getResultList();
        for(TopCategory topCategory: topCategories){
            topCategory.setChildCategories(entityManager.get().createQuery("SELECT c FROM Category c WHERE " +
                    "c.topCategory = :topCategory", Category.class).setParameter("topCategory", topCategory)
                    .getResultList());
        }
        return topCategories;
    }
}
