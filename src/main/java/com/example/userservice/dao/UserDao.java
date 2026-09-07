package com.example.userservice.dao;

import com.example.userservice.model.User;

import java.util.List;

public interface UserDao {
    void save(User user);

    void update(User user);

    void remove(Long id);

    User findById(Long id);

    List<User> findAll();

}
