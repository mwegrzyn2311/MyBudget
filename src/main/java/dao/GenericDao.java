package dao;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

public abstract class GenericDao<T> {
	protected Provider<EntityManager> entityManager;
	private final Class< T > clazz;

	public GenericDao(Provider<EntityManager> entityManager, Class<T> clazz) {
	    this.entityManager = entityManager;
	    this.clazz = clazz;
	}

    @Transactional
    public void save(final T entity) throws PersistenceException {
		entityManager.get().persist(entity);
	}

    @Transactional
    public void update(T entity) {
        entityManager.get().merge(entity);
    }

    @Transactional
    public void delete(T entity) {
        entityManager.get().remove(entity);
    }

    public Optional<T> findOne(Long id) {
	    return Optional.of(entityManager.get().find(clazz, id));
    }

	public List<T> findAll() {
		return entityManager.get().createQuery("from " + clazz.getName()).getResultList();
	}

    public void refresh(T entity) {
	    entityManager.get().refresh(entity);
    }
}
