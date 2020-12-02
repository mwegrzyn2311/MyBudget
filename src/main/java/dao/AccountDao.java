package dao;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import model.Account;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class AccountDao extends GenericDao<Account> {
    @Inject
    public AccountDao(Provider<EntityManager> entityManager) {
        super(entityManager, Account.class);
    }


    public Optional<Account> create(String name, String accountNumber, BigDecimal initialBalance) {
        try {
            Account account = new Account(name, accountNumber, initialBalance, new LinkedList<>());
            save(account);
            return Optional.of(account);
        } catch(PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Account> findByName(final String name) {
        try {
            Account account = entityManager.get().createQuery("SELECT c FROM Account c WHERE c.name = :name", Account.class)
                    .setParameter("name", name).getSingleResult();
            return Optional.of(account);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
