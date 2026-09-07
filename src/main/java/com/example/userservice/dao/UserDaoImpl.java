package com.example.userservice.dao;

import com.example.userservice.config.HibernateSession;
import com.example.userservice.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class UserDaoImpl implements UserDao {


    @Override
    public void save(User user) {
        try (Session session = HibernateSession.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(user);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    @Override
    public void update(User user) {
        try (Session session = HibernateSession.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.merge(user);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
            }
        }
    }

    @Override
    public void remove(Long id) {
        try (Session session = HibernateSession.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {

                User user = session.find(User.class, id);
                if (user == null) {
                    throw new IllegalArgumentException("Пользователь с id=" + id + " не найден");
                }
                session.remove(user);
                transaction.commit(); // <-- обязательно!
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                if (!(e instanceof IllegalArgumentException)) {
                    throw new RuntimeException("Ошибка при удалении пользователя", e);
                } else {
                    throw e;
                }
            }
        }
    }

    @Override
    public User findById(Long id) {
        try (Session session = HibernateSession.getSessionFactory().openSession()) {
            return session.find(User.class, id);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateSession.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User", User.class);
            return query.list();
        }
    }
}
