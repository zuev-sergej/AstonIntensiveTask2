package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.dao.UserDaoImpl;
import com.example.userservice.model.User;
import com.example.userservice.validators.UserValidator;

import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserValidator userValidator;

    public UserServiceImpl(UserDao userDao, UserValidator validator) {
        this.userDao = userDao;
        this.userValidator = validator;
    }

    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
        this.userValidator = new UserValidator();
    }

    @Override
    public void createUser(User user) {
        userValidator.validate(user);
        userDao.save(user);
    }

    @Override
    public User getById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public void updateUser(User user) {
        userValidator.validate(user);
        userDao.update(user);

    }

    @Override
    public void deleteUser(Long id) {
        userDao.remove(id);

    }
}
