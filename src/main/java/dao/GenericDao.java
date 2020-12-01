package dao;

import model.Account;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public abstract class GenericDao<T> {
	protected EntityManager entityManager;
	protected Class< T > clazz;

	public GenericDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void save(final T object) throws PersistenceException {
		entityManager.getTransaction().begin();
		entityManager.persist(object);
		entityManager.getTransaction().commit();
	}

	public List<T> findAll() {
		return entityManager.createQuery("from " + clazz.getName()).getResultList();
	}
}
