package pl.training.jpa.entity;

import pl.training.jpa.common.EntityTest;

import java.util.Date;

public class PaymentTest extends EntityTest<Payment> {

    @Override
    protected void initializeEntity() {
        entity = Payment.builder()
                .status(PaymentStatus.STARTED)
                .timestamp(new Date())
                .build();
    }

}
