package pl.training.jpa.examples;

import org.junit.jupiter.api.Test;
import pl.training.jpa.common.BaseTest;
import pl.training.jpa.entity.Order;

import javax.persistence.LockModeType;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionsTest extends BaseTest {

    private final Order order = Order.builder()
            .owner("Łukasz")
            .description("Mój opis")
            .build();

    @Test
    void shouldRollbackTransactionWhenExceptionIsThrown() {
        withTransaction(entityManager -> {
            entityManager.persist(order);
            throw new RuntimeException();
        });
        withTransaction(entityManager -> {
            var result = entityManager.createQuery("select o from Order o", Order.class).getResultList();
            assertEquals(0, result.size());
        });
    }

    @Test
    void shouldWaitForTransactionToFinish() {
        withTransaction(entityManager -> entityManager.persist(order));
        var orderId = order.getId();
        var taskOne = new UpdateOrderTask(orderId, 2, 5);
        var taskTwo = new UpdateOrderTask(orderId, 3, 1);
        execute(List.of(taskOne, taskTwo));
        withTransaction(entityManager -> {
            var order = entityManager.find(Order.class, orderId);
            assertEquals(taskTwo.getTotalAmount(), order.getTotalValue());
        });
    }

}
