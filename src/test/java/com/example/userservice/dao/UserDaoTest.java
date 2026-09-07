package com.example.userservice.dao;

import com.example.userservice.config.HibernateSession;
import com.example.userservice.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserDaoTest {

    @Container
    private static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:15")
                    .withDatabaseName("user_test")
                    .withUsername("test")
                    .withPassword("test");

    private static SessionFactory testSessionFactory;
    private UserDaoImpl userDaoImpl;

    @BeforeAll
    public static void setUp() {
        testSessionFactory = HibernateSession.getSessionFactory(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

    }

    @AfterAll
    static void closeSessionFactory() {
        if (testSessionFactory != null) {
            testSessionFactory.close();
        }
    }

    @BeforeEach
    void setUpEach() {
        userDaoImpl = new UserDaoImpl(testSessionFactory);
    }

    @AfterEach
    void cleanDatabase() {
        try (Session session = testSessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            session.createMutationQuery("delete from User").executeUpdate();

            transaction.commit();
        }
    }

    @Test
    void testSaveUser() {
        var user = new User("Anton", "anton@mail.ru", 25);
        userDaoImpl.save(user);

        Optional<User> saveUserOpt = userDaoImpl.findById(user.getId());

        User saveUser = saveUserOpt.get();

        assertNotNull(saveUser, "Пользователь не найден в БД после сохранения");
        assertEquals("Anton", saveUser.getName());
        assertEquals("anton@mail.ru", saveUser.getEmail());
        assertEquals(25, saveUser.getAge());
    }

    @Test
    void testUpdateUser() {
        var user = new User("oldUser", "oldUser@mail.ru", 25);
        userDaoImpl.save(user);

        user.setName("NewName");
        user.setEmail("newName@mail.ru");
        user.setAge(21);

        userDaoImpl.update(user);

        Optional<User> updateUserOpt = userDaoImpl.findById(user.getId());

        assertTrue(updateUserOpt.isPresent(), "Пользователь должен быть найден после обновления");

        User updateUser = updateUserOpt.get();

        assertEquals("NewName", updateUser.getName());
        assertEquals("newName@mail.ru", updateUser.getEmail());
        assertEquals(21, updateUser.getAge());
    }

    @Test
    void testRemoveUser() {
        var user = new User("Anton", "anton@mail.ru", 25);
        userDaoImpl.save(user);

        assertNotNull(user, "Пользователь не найден в БД после сохранения");

        userDaoImpl.remove(user.getId());

        Optional<User> found = userDaoImpl.findById(user.getId());

        assertFalse(found.isPresent(), "Пользователь должен быть удалён из БД");
    }

    @Test
    void findByIdUser() {
        var user = new User("Anton", "anton@mail.ru", 25);
        userDaoImpl.save(user);

        Optional<User> found = userDaoImpl.findById(user.getId());
        assertNotNull(found, "Пользователь найден в БД после сохранения");
    }

    @Test
    void findAllUsers() {
        var user1 = new User("1", "1@mail.ru", 11);
        var user2 = new User("2", "2@mail.ru", 12);
        var user3 = new User("3", "3@mail.ru", 13);

        userDaoImpl.save(user1);
        userDaoImpl.save(user2);
        userDaoImpl.save(user3);

        List<User> list = userDaoImpl.findAll();

        var expected = List.of(user1, user2, user3);
        assertIterableEquals(expected, list);
    }
}
