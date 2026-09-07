package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.model.User;
import com.example.userservice.validators.UserValidator;
import com.example.userservice.validators.ValidationException;

import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserValidator userValidator;

    public UserServiceImpl(UserDao userDao, UserValidator validator) {
        this.userDao = userDao;
        this.userValidator = validator;
    }

    @Override
    public User createUser(User user) {
        userValidator.validate(user);
        userDao.save(user);
        return user;
    }

    @Override
    public User getById(Long id) {
        return userDao.findById(id)
                .orElseThrow(() -> new ValidationException("Не найден пользователь с id: " + id));
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
