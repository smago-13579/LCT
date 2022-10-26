package com.example.controller;

import com.example.dto.AuthRequest;
import com.example.dto.UserDto;
import com.example.models.AuthOkResponse;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController extends BaseController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/user/register")
    public void register(@RequestBody UserDto userDto) {
        userService.registerUser(userDto);
    }

    @PostMapping(path = "/user/auth")
    public AuthOkResponse authorize(@RequestBody AuthRequest authRequest) throws Exception {
        return userService.authorize(authRequest);
    }
}
