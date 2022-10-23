package com.example.controller;

import com.example.dto.UserForm;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController extends BaseController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "users/register")
    public void register(UserForm userForm) {
        userService.registerUser(userForm);
    }
}
