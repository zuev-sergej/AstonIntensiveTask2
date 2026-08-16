package com.example.userservice.config;

import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateSession {
    private static final Logger logger = LoggerFactory.getLogger(HibernateSession.class);
    @Getter
    private static final SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration().configure();
            sessionFactory = configuration.buildSessionFactory();
            logger.debug("Hibernate SessionFactory успешно создан.");
        } catch (Throwable e) {
            logger.info("Ошибка инициализации SessionFactory");
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
            logger.debug("Hibernate SessionFactory закрыт.");
        }
    }
}
