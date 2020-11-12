package pl.training.cdi;

import lombok.Setter;
import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Log
@Transactional(Transactional.TxType.REQUIRED)
@ApplicationScoped
public class AccountsService {

    @PersistenceContext(unitName = "training-hibernate-unit")
    @Setter
    private EntityManager entityManager;

    public void createAccount() {
        log.info("### Init");
        var account = new Account(0L, "00000000000000001");
        entityManager.persist(account);
    }

}
