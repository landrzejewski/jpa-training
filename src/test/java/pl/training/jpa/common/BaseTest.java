package pl.training.jpa.common;

import org.junit.jupiter.api.AfterEach;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.function.Consumer;

public abstract class BaseTest {

    protected EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("training-persistence-unit");

    protected void withTransaction(Consumer<EntityManager> task) {
        var entityManager = entityManagerFactory.createEntityManager();
        var transaction = entityManager.getTransaction();
        transaction.begin();
        task.accept(entityManager);
        transaction.commit();
        entityManager.close();
    }

    @AfterEach
    void onClose() {
        entityManagerFactory.close();
    }

}