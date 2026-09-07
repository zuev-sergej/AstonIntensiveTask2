package com.example.userservice.dao;

import com.example.userservice.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> save(User user);

    void update(User user);

    void remove(Long id);

    Optional<User> findById(Long id);

    List<User> findAll();

}
