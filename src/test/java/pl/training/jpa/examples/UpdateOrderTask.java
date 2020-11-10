package pl.training.jpa.examples;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import pl.training.jpa.common.Task;
import pl.training.jpa.entity.Order;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Log
@Getter
@Setter
@RequiredArgsConstructor
public class UpdateOrderTask implements Task {

    private final Long orderId;
    private final Integer firstSleepTime;
    private final Integer secondSleepTime;
    private final Integer totalAmount = new Random().nextInt(100);

    private EntityManager entityManager;
    private CountDownLatch countDownLatch;

    @Override
    public void run() {
        var threadName = Thread.currentThread().getName();
        log.info("##### " + threadName + " started");
        try {
            var transaction = entityManager.getTransaction();
            transaction.begin();
            TimeUnit.SECONDS.sleep(firstSleepTime);
            log.info("##### " + threadName + " before lock ");
            var order = entityManager.find(Order.class, orderId, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            log.info("##### " + threadName + " after lock ");
            order.setTotalValue(totalAmount);
            log.info("##### " + threadName + " before commit " + order);
            transaction.commit();
            log.info("##### " + threadName + " after commit");
            TimeUnit.SECONDS.sleep(secondSleepTime);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
            countDownLatch.countDown();
        }
    }

}
