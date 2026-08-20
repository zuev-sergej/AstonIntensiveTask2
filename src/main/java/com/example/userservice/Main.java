package com.example.userservice;

import com.example.userservice.config.HibernateSession;
import com.example.userservice.console.Console;
import com.example.userservice.dao.UserDao;
import com.example.userservice.dao.UserDaoImpl;
import com.example.userservice.service.UserService;
import com.example.userservice.service.UserServiceImpl;
import com.example.userservice.validators.UserValidator;
import org.hibernate.SessionFactory;

public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateSession.getSessionFactory();

        UserDao userDao = new UserDaoImpl(sessionFactory);

        UserValidator userValidator = new UserValidator();

        UserService userService = new UserServiceImpl(userDao, userValidator);

        Console console = new Console(userService);

        console.run();
    }
}
