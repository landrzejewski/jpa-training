package pl.training.jpa.examples;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.training.jpa.common.BaseTest;
import pl.training.jpa.entity.Order;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionsTest extends BaseTest {

    private final Order order = Order.builder()
            .owner("Łukasz")
            .description("Mój opis")
            .build();

    @Test
    void shouldRollbackTransaction() {
        withTransaction(entityManager -> {
            entityManager.persist(order);
            throw new RuntimeException();
        });
        withTransaction(entityManager -> {
            var result = entityManager.createQuery("select o from Order o", Order.class).getResultList();
            assertEquals(0, result.size());
        });
    }



}
