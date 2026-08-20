package com.example.userservice.validators;

import com.example.userservice.model.User;

import java.util.regex.Pattern;

public class UserValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@[\\w-]+(\\.[\\w-]+)*\\.[a-z]{2,}$");

    public void validate(User user) {
        if (user == null) {
            throw new ValidationException("Пользователь не может быть null");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new ValidationException("Имя пользователя обязательно для заполнения");
        }
        if (user.getName().length() > 100) {
            throw new ValidationException("Имя не должно превышать 100 символов");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Email обязателен для заполнения");
        }
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new ValidationException("Некорректный формат email: " + user.getEmail());
        }
        Integer age = user.getAge();
        if (age == null) {
            throw new ValidationException("Возраст обязателен для заполнения");
        }
        if (user.getAge() <= 0 || user.getAge() > 150) {
            throw new ValidationException("Некорректный возраст");
        }
    }
}
