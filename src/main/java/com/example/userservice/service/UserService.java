package com.example.userservice.service;

import com.example.userservice.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User getById(Long id);

    List<User> getAllUsers();

    void updateUser(User user);

    void deleteUser(Long id);
}
