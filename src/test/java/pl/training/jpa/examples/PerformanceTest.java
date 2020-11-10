package pl.training.jpa.examples;

import lombok.extern.java.Log;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import pl.training.jpa.common.BaseTest;
import pl.training.jpa.entity.Order;
import pl.training.jpa.entity.Post;
import pl.training.jpa.entity.Queries;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Log
public class PerformanceTest extends BaseTest {

    private static final int SAMPLES = 500;

    @Test
    void shouldMeasureAddingOrderPerformanceForSingleTransaction() {
        var timer = metricRegistry.timer(getClass().getName());
        withTransaction(entityManager -> {
            for (int sampleNo = 1; sampleNo <= SAMPLES; sampleNo++) {
                var startTime = System.nanoTime();
                entityManager.persist(Order.builder()
                        .owner(UUID.randomUUID().toString())
                        .description("Jakiś opis")
                        .build());
                timer.update(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
            }
        });
        reporter.report();
    }

    @Test
    void shouldMeasureAddingOrderPerformanceForManyTransactions() {
        var timer = metricRegistry.timer(getClass().getName());
        for (int sampleNo = 1; sampleNo <= SAMPLES; sampleNo++) {
            var startTime = System.nanoTime();
            withTransaction(entityManager -> {
                entityManager.persist(Order.builder()
                        .owner(UUID.randomUUID().toString())
                        .description("Jakiś opis")
                        .build());
            });
            timer.update(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        }
        reporter.report();
    }

    @Test
    void shouldShowStatistics() {
        var statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        withTransaction(entityManager -> {
            entityManager.persist(Order.builder()
                    .owner(UUID.randomUUID().toString())
                    .description("Jakiś opis")
                    .build());
        });
        log.info(statistics.toString());
    }

    @Test
    void shouldBatchCreatingPosts() {
        withTransaction(entityManager -> {
            var timer = metricRegistry.timer(getClass().getName());
            var startTime = System.nanoTime();
            for (int index = 0; index < 10_000; index++) {
                var post = new Post(UUID.randomUUID().toString(), UUID.randomUUID().toString());
                entityManager.persist(post);
                if (index % 1000 == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            timer.update(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        });
        reporter.report();
    }

    @Test
    void shouldUpdateOrDeleteDatabaseRows() {
        withTransaction(entityManager -> {
            entityManager.createNamedQuery(Queries.POSTS_REST_TEXT).executeUpdate();
            entityManager.createNamedQuery(Post.REST_TEXT).executeUpdate();
            entityManager.createQuery("delete Post p where p.id > 10").executeUpdate();
        });
    }

}
