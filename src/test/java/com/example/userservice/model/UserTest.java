package com.example.userservice.model;

import com.example.userservice.config.HibernateSession;
import jakarta.persistence.PersistenceException;
import org.hibernate.PropertyValueException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
public class UserTest {
    @Container
    private static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:15")
                    .withDatabaseName("user_test")
                    .withUsername("test")
                    .withPassword("test");

    protected static SessionFactory testSessionFactory;
    private Session testSession;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testSessionFactory = HibernateSession.getSessionFactory(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        testSession = testSessionFactory.openSession();

        testTransaction = testSession.beginTransaction();
    }

    @AfterEach
    void cleanDatabase() {
        if (testTransaction != null && testTransaction.getStatus() == TransactionStatus.ACTIVE) {
            testTransaction.rollback();
        }
        testSession.close();
    }


    @Test
    void shouldSaveUser() {
        User user = new User("user", "user@mail.ru", 22);

        testSession.persist(user);
        testSession.flush();

        assertNotNull(user.getId());
    }

    @Test
    void shouldBeFailedWithTheSameEmail() {
        User user = new User("user", "user@mail.ru", 22);
        User user2 = new User("user2", "user@mail.ru", 24);
        testSession.persist(user);
        testSession.flush();
        assertThrows(PersistenceException.class, () -> {
            testSession.persist(user2);
        });
    }

    @Test
    void shouldBeFailedWithEmptyName() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setAge(34);

        assertThrows(PropertyValueException.class, () -> {
            testSession.persist(user);
            testSession.flush();
            testTransaction.commit();

        });
    }

    @Test
    void shouldBeFailedWithEmptyEmail() {
        User user = new User();
        user.setName("user");
        user.setAge(34);

        assertThrows(PropertyValueException.class, () -> {
            testSession.persist(user);
            testSession.flush();
            testTransaction.commit();

        });
    }
}
