package pl.training.jpa.entity;

import pl.training.jpa.common.EntityTest;
import pl.training.jpa.common.LocalMoney;

import java.util.Date;

public class PaymentTest extends EntityTest<Payment> {

    @Override
    protected void initializeEntity() {
        entity = Payment.builder()
                .status(PaymentStatus.STARTED)
                //.timestamp(new Date())
                .value(LocalMoney.of(1_000))
                .build();
    }

}
