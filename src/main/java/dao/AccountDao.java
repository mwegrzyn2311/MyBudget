package dao;

import model.Account;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

public class AccountDao extends GenericDao<Account> {
    @Inject
    public AccountDao(EntityManager entityManager) {
        super(entityManager);
        clazz = Account.class;
    }


    public Optional<Account> create(String name) {
        try {
            save(new Account());
            return findByName(name);
        } catch(PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Account> findByName(final String name) {
        try {
            Account account = entityManager.createQuery("SELECT c FROM Account c WHERE c.name = :name", Account.class)
                    .setParameter("name", name).getSingleResult();
            return Optional.of(account);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Account> findById(final int id) {
        try {
            Account account = entityManager.createQuery("SELECT c FROM Account c WHERE c.id = :id", Account.class)
                    .setParameter("id", id).getSingleResult();
            return Optional.of(account);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
