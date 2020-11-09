package pl.training.jpa.examples;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import pl.training.jpa.common.BaseTest;

public class ConnectionTest extends BaseTest {

    @Test
    void shouldConnectToDatabase() {
        withTransaction(entityManager -> {});
    }

    @Test
    void shouldAccessHibernateAPI() {
        withTransaction(entityManager -> {
            var sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);
            var session = entityManager.unwrap(Session.class);
        });
    }

}
