package pl.training.jpa.examples;

import pl.training.jpa.common.EntityTest;
import pl.training.jpa.common.LocalMoney;
import pl.training.jpa.entity.Payment;
import pl.training.jpa.entity.PaymentStatus;

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
