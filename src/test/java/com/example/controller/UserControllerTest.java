package com.example.controller;

import com.example.dto.AuthRequest;
import com.example.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerUserTest() {
        registerUser("abc@ya.ru", "abcdefghij", "firstName", "lastName", HttpStatus.OK);
        registerUser("abc", "abcdefghij", "firstName", "lastName", HttpStatus.BAD_REQUEST);
    }

    @Test
    void authorizeUserTest() {
        registerUser("firstEmail@ya.ru", "abcdefghij",
                "firstName", "lastName", HttpStatus.OK);
        authorizeUser("firstEmail@ya.ru", "abcdefghij", HttpStatus.OK);
    }

    @SneakyThrows
    private MvcResult authorizeUser(String email, String password, HttpStatus status) {
        return this.mockMvc.perform(post("/user/auth")
                        .content(getUserCredsAsString(email, password))
                        .header("Content-Type", "application/json"))
                .andDo(print())
                .andExpect(status().is(status.value())).andReturn();
    }

    @SneakyThrows
    private void registerUser(String email, String password, String firstName,
                              String lastName, HttpStatus status) {
        String content = getUserCredsAsString(email, password, firstName, lastName);
        this.mockMvc.perform(post("/user/register")
                        .content(content)
                        .header("Content-Type", "application/json"))
                .andDo(print())
                .andExpect(status().is(status.value())).andReturn();
    }

    private String getUserCredsAsString(String email, String password, String firstName,
                                        String lastName) throws Exception {
        return mapper.writeValueAsString(UserDto.builder().email(email)
                .password(password).firstName(firstName).lastName(lastName).build());
    }

    private String getUserCredsAsString(String email, String password) throws Exception {
        return mapper.writeValueAsString(new AuthRequest(email, password));
    }
}
