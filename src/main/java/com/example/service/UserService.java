package com.example.service;

import com.example.dto.AuthRequest;
import com.example.dto.UserDto;
import com.example.models.AuthOkResponse;

public interface UserService {
    void registerUser(UserDto userDto);
    AuthOkResponse authorize(AuthRequest authRequest) throws Exception;

    void verifyUser(String id, String code);
}
