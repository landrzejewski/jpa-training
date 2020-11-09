package pl.training.jpa.examples;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import pl.training.jpa.common.Task;
import pl.training.jpa.entity.Order;

import javax.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Log
@Setter
@RequiredArgsConstructor
public class UpdateOrder implements Task {

    private final Long orderId;
    private final Random random = new Random();

    private EntityManager entityManager;
    private CountDownLatch countDownLatch;

    @SneakyThrows
    @Override
    public void run() {
        var threadName = Thread.currentThread().getName();
        log.info(threadName + " started");
        var transaction = entityManager.getTransaction();
        var order = entityManager.find(Order.class, orderId);
        TimeUnit.SECONDS.sleep(random.nextInt(5));
        order.setTotalValue(random.nextLong());
        log.info(threadName + " before commit " + order);
        transaction.commit();
        log.info(threadName + " after commit");
        entityManager.close();
        countDownLatch.countDown();
    }

}
