package com.example.userservice.console;

import com.example.userservice.config.HibernateSession;
import com.example.userservice.model.User;
import com.example.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Console {
    private static final Logger logger = LoggerFactory.getLogger(Console.class);
    private final UserService userService;
    private final Scanner scanner;

    public Console(UserService userService) {
        this.userService = userService;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("=== Приложение UserService ===");

        boolean exit = false;
        while (!exit) {
            printMenu();
            int choice = readInt("Выберите действие (1–6): ");

            try {
                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> readUserById();
                    case 3 -> readAllUsers();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 6 -> {
                        exit = true;
                        HibernateSession.closeSessionFactory();
                        System.out.println("Выход из программы...");
                    }
                    default -> System.out.println("Неверный выбор. Попробуйте снова.");
                }
            } catch (Exception e) {
                System.out.println("Произошла ошибка: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println("\n--- Меню ---");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Получить пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("6. Выход");
        System.out.println("------------");
    }

    void createUser() {
        System.out.println("--- Создание нового пользователя ---");
        String name = readString("Введите имя: ");
        String email = readString("Введите email: ");
        String ageInput = readString("Введите возраст: ");

        Integer age = null;
        if (!ageInput.isBlank()) {
            try {
                age = Integer.parseInt(ageInput);
            } catch (NumberFormatException e) {
                System.out.println("Некорректный возраст");
                return;
            }
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        user.setCreated_at(LocalDateTime.now());

        try {
            userService.createUser(user);
            System.out.println("Добавлен новый пользователь: " + name + " (" + email + "), возраст: " + age);
        } catch (IllegalArgumentException e) {
            String msg = "Ошибка валидации: " + e.getMessage();
            logger.info(msg);
            System.out.println(msg);
        } catch (Exception e) {
            String msg = "Произошла критическая ошибка при сохранении: " + e.getMessage();
            logger.error(msg, e);
            System.out.println(msg);
        }
    }

    private void readUserById() {
        Long id = readLong("Введите ID пользователя: ");
        User user = userService.getById(id);
        if (user != null) {
            logger.info("Найден пользователь: {}", user);
        } else {
            logger.error("Пользователь с ID {} не найден.", id);
        }
    }

    private void readAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Список пользователей пуст.");
            return;
        }
        System.out.println("--- Список пользователей ---");
        for (User u : users) {
            System.out.println(u);
        }
    }

    private void updateUser() {
        System.out.println("--- Обновление пользователя ---");
        Long id = readLong("Введите ID пользователя для обновления: ");

        User existingUser = userService.getById(id);
        if (existingUser == null) {
            logger.info("Пользователь с ID {} не найден. Обновление невозможно.", id);
            return;
        }

        String nameInput = readString("Новое имя (оставьте пустым, чтобы не менять): ");
        String emailInput = readString("Новый email (оставьте пустым, чтобы не менять): ");
        String ageInput = readString("Новый возраст (оставьте пустым, чтобы не менять): ");

        if (!nameInput.trim().isEmpty()) {
            existingUser.setName(nameInput);
        }
        if (!emailInput.trim().isEmpty()) {
            existingUser.setEmail(emailInput);
        }

        if (!ageInput.trim().isEmpty()) {
            try {
                int newAge = Integer.parseInt(ageInput.trim());
                if (newAge < 0 || newAge > 150) {
                    logger.error("Некорректный возраст: {}. Возраст должен быть от 0 до 150.", newAge);
                } else {
                    existingUser.setAge(newAge);
                }
            } catch (NumberFormatException e) {
                logger.error("Введено некорректное значение возраста: '{}'. Возраст не будет изменён.", ageInput);
            }
        }
        if (nameInput.trim().isEmpty() && emailInput.trim().isEmpty() && ageInput.trim().isEmpty()) {
            System.out.println("Ничего не изменено.");
            return;
        }

        try {
            userService.updateUser(existingUser);
            System.out.println("Пользователь успешно обновлен.");
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка валидации: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при обновлении: {}", e.getMessage());
        }
    }

    private void deleteUser() {
        Long id = readLong("Введите ID пользователя для удаления: ");
        try {
            if (id <= 0) {
                System.out.println("Ошибка: ID должен быть положительным числом.");
            }

            userService.deleteUser(id);
            System.out.println("Пользователь с ID " + id + " удален.");
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка: {}", e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Ошибка при удалении: {}", e.getMessage());
        }
    }

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } else {
                System.out.println("Пожалуйста, введите целое число.");
                scanner.nextLine();
            }
        }
    }

    private long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextLong()) {
                long value = scanner.nextLong();
                scanner.nextLine();
                return value;
            } else {
                System.out.println("Пожалуйста, введите корректное число (ID).");
                scanner.nextLine();
            }
        }
    }
}
