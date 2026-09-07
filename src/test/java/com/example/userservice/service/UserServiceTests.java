package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.model.User;
import com.example.userservice.validators.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserDao mockUserDao;

    @Mock
    private UserValidator mockUserValidator;

    @InjectMocks
    private UserServiceImpl userServiceImpl;


    @Test
    void createUser_success() {
        User input = new User("Gerda", "gerda@mail.ru", 12);
        User expected = new User("Gerda", "gerda@mail.ru", 12);

        when(mockUserDao.save(input)).thenReturn(Optional.of(expected));

        doNothing().when(mockUserValidator).validate(any(User.class));

        User result = userServiceImpl.createUser(input);

        assertEquals(expected, result);
        verify(mockUserDao, times(1)).save(input);
        verify(mockUserValidator, times(1)).validate(any(User.class));
    }

    @Test
    void createUser_failure() {
        User input = new User("Gerda", "gerda@mail.ru", 12);
        when(mockUserDao.save(input)).thenThrow(new RuntimeException("DataBase error"));

        assertThrows(RuntimeException.class, () -> userServiceImpl.createUser(input));
        verify(mockUserDao, times(1)).save(input);
    }

    @Test
    void getByIdUser_success() {
        Long id = 1L;
        User input = new User("Gerda", "gerda@mail.ru", 12);
        when(mockUserDao.findById(id)).thenReturn(Optional.of(input));

        User result = userServiceImpl.getById(id);

        assertEquals(input, result);
    }

    @Test
    void getByIdUser_failure() {
        Long failId = 999L;

        when(mockUserDao.findById(failId)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> userServiceImpl.getById(failId));

        verify(mockUserDao).findById(failId);
    }

    @Test
    void getAllUsers() {
        String name1 = "User_1_" + System.currentTimeMillis();
        String name2 = "User_2_" + System.currentTimeMillis();

        User user1 = new User(name1, name1 + "@mail.ru", 11);
        User user2 = new User(name2, name2 + "@mail.ru", 12);

        List<User> expectedList = List.of(user1, user2);

        when(mockUserDao.findAll()).thenReturn(expectedList);

        List<User> allUsers = userServiceImpl.getAllUsers();

        assertEquals(2, allUsers.size());
        assertEquals(expectedList, allUsers);

        verify(mockUserDao).findAll();
    }

    @Test
    void updateUserTest() {
        String oldName = "OldUser" + System.currentTimeMillis();
        String oldEmail = oldName + "@mail.ru";
        int oldAge = 23;

        User user = new User(oldName, oldEmail, oldAge);
        user.setId(12L);

        when(mockUserDao.findById(anyLong())).thenReturn(Optional.of(user));

        mockUserDao.save(user);

        Long id = user.getId();
        assertNotNull(id, "ID должен быть сгенерирован");

        String newName = "UpdatedName_" + System.currentTimeMillis();
        String newEmail = newName + "@mail.ru";
        int newAge = 21;

        user.setName(newName);
        user.setEmail(newEmail);
        user.setAge(newAge);

        mockUserDao.save(user);

        Optional<User> foundOpt = mockUserDao.findById(id);
        User found = foundOpt.orElseThrow(() -> new AssertionError("Пользователь с ID " + id + " не найден после обновления"));


        assertNotNull(found, "Пользователь с ID " + id + " должен существовать после обновления");

        assertEquals(newName, found.getName(), "Имя должно быть обновлено");
        assertEquals(newEmail, found.getEmail(), "Email должен быть обновлён");
        assertEquals(newAge, found.getAge(), "Возраст должен быть обновлён");

        assertEquals(id, found.getId(), "ID должен остаться прежним");

        assertNotEquals(oldName, found.getName(), "Имя не должно остаться старым");
        assertNotEquals(oldEmail, found.getEmail(), "Email не должен остаться старым");
        assertNotEquals(oldAge, found.getAge(), "Возраст не должен остаться старым");
    }

}
