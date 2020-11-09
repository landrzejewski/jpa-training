package pl.training.jpa.examples;

import org.junit.jupiter.api.Test;
import pl.training.jpa.common.BaseTest;
import pl.training.jpa.entity.Order;

import javax.persistence.RollbackException;
import javax.validation.Validation;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationTest extends BaseTest {

    private final Order order = Order.builder()
            .owner("Łukasz")
            .description("-sdfdsfsdfsdfds")
            .build();

    @Test
    void shouldValidateOrderBeforePersist() {
        assertThrows(RollbackException.class, () -> withTransaction(entityManager -> entityManager.persist(order)));
    }

    @Test
    void shouldValidateOrder() {
        var factory = Validation.buildDefaultValidatorFactory();
        var validator = factory.getValidator();
        var constraintValidations= validator.validate(order);
        assertEquals(1, constraintValidations.size());
        System.out.println(constraintValidations);
    }

}
