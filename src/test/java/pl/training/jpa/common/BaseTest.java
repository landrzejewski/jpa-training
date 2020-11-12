package pl.training.jpa.common;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.junit.jupiter.api.AfterEach;
import org.slf4j.LoggerFactory;

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

    protected EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("training-eclipselink-unit");
            // = Persistence.createEntityManagerFactory("training-hibernate-unit");
    protected MetricRegistry metricRegistry = new MetricRegistry();
    protected Slf4jReporter reporter = Slf4jReporter
            .forRegistry(metricRegistry)
            .outputTo(LoggerFactory.getLogger(getClass()))
            .build();

    protected void withTransaction(Consumer<EntityManager> task) {
        var entityManager = entityManagerFactory.createEntityManager();
        var transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            task.accept(entityManager);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
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