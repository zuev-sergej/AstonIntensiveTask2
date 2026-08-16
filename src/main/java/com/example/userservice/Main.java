package com.example.userservice;

import com.example.userservice.console.Console;
import com.example.userservice.service.UserService;
import com.example.userservice.service.UserServiceImpl;

public class Main {
    public static void main(String[] args) {

        UserService userService = new UserServiceImpl();

        Console console = new Console(userService);

        console.run();
    }
}
