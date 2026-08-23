package com.example.userservice.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateSession {
    private static final Logger logger = LoggerFactory.getLogger(HibernateSession.class);
    private static SessionFactory sessionFactory;

    private HibernateSession() {
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory != null) {
            return sessionFactory;
        }

        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
            logger.debug("Hibernate SessionFactory успешно создан (режим приложения).");
            return sessionFactory;
        } catch (Exception e) {
            logger.error("Не удалось создать SessionFactory из hibernate.cfg.xml. " +
                    "Убедитесь, что файл существует в src/main/resources и содержит корректные данные.", e);
            throw new IllegalStateException(
                    "SessionFactory не инициализирован. Проверьте hibernate.cfg.xml или используйте метод с параметрами.", e
            );
        }
    }

    public static SessionFactory getSessionFactory(String jdbcUrl, String username, String password) {
        if (sessionFactory != null) {
            logger.warn("SessionFactory уже инициализирован. Возвращаем существующий экземпляр.");
            return sessionFactory;
        }
        try {
            Configuration configuration = new Configuration().configure();

            configuration.setProperty("hibernate.connection.url", jdbcUrl);
            configuration.setProperty("hibernate.connection.username", username);
            configuration.setProperty("hibernate.connection.password", password);
            configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");  // Для интеграционных тестов

            sessionFactory = configuration.buildSessionFactory();

            logger.debug("Hibernate SessionFactory успешно создан.");

            return sessionFactory;
        } catch (Throwable e) {
            logger.error("Ошибка инициализации SessionFactory", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            logger.debug("Hibernate SessionFactory закрыт.");
            sessionFactory = null;
        }
    }
}
