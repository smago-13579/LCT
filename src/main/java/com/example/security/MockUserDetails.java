package com.example.security;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class MockUserDetails extends User {
    private Long id;

    public MockUserDetails(String username, String password, Long id) {
        super(username, password, List.of());
        this.id = id;
    }
}
