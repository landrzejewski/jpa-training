package pl.training.jpa.common;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.junit.jupiter.api.AfterEach;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

@Log
public abstract class BaseTest {

    protected EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("training-persistence-unit");

    protected void withTransaction(Consumer<EntityManager> task) {
        var entityManager = entityManagerFactory.createEntityManager();
        var transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            task.accept(entityManager);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }
        entityManager.close();
    }

    @SneakyThrows
    protected void execute(List<Task> tasks) {
        var simulatedUsers = tasks.size();
        var countDownLatch = new CountDownLatch(simulatedUsers);
        var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(simulatedUsers);
        tasks.forEach(task -> {
            task.setEntityManager(entityManagerFactory.createEntityManager());
            task.setCountDownLatch(countDownLatch);
            executor.submit(task);
        });
        countDownLatch.await();
        log.info("Completed");
    }

    @AfterEach
    void onClose() {
        entityManagerFactory.close();
    }

}