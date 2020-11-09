package pl.training.jpa.entity;

import pl.training.jpa.common.EntityTest;

public class PaymentTest extends EntityTest<Payment> {

    @Override
    protected void initializeEntity() {
        entity = Payment.builder()
                .status(PaymentStatus.STARTED)
                .build();
    }

}
