package dao;

import model.Account;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class GenericDao<T extends Serializable> {
	protected EntityManager entityManager;
	private final Class< T > clazz;

	public GenericDao(EntityManager entityManager, Class<T> clazz) {
	    this.entityManager = entityManager;
	    this.clazz = clazz;
	}

	public void save(final T entity) throws PersistenceException {
		entityManager.getTransaction().begin();
		entityManager.persist(entity);
		entityManager.getTransaction().commit();
	}

	public void update(T entity) {
        entityManager.getTransaction().begin();
        entityManager.merge(entity);
        entityManager.getTransaction().commit();
    }

    public void delete(T entity) {
        entityManager.getTransaction().begin();
        entityManager.remove(entity);
        entityManager.getTransaction().commit();
    }

    public Optional<T> findOne(Long id) {
	    return Optional.of(entityManager.find(clazz, id));
    }

	public List<T> findAll() {
		return entityManager.createQuery("from " + clazz.getName()).getResultList();
	}

	public void refresh(T entity) {
	    entityManager.refresh(entity);
    }
}
