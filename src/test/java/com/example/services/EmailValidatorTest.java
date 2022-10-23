package com.example.services;

import com.example.dto.UserForm;
import com.example.models.BadInputParameters;
import com.example.service.UserService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class EmailValidatorTest {

    @Autowired
    private UserService userService;
    private String password = "password";

    @ParameterizedTest
    @ValueSource(strings = {"abc@ya.ru", "a@yandex.ru", "smago@STUDENT.21-school_ru", "123@12.ru", "1RU@com",
            "abc@abd123.NET", "(x)@RU", "?x?@COM", "!x!@google.ru", "Z$%^@i.WILL.back"})
    void test_emailValidator_withValidEmail(String email) {
        assertDoesNotThrow(() -> userService.validateUserForm(new UserForm(email, password)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc_ya.ru", "a@", "smago@STUDENT@21 school.ru", "123 @12ru!", "1@RU?\t",
            "abc@abd123.(NET)\n@", "(x)@(RU)_@\n123", "?x?@COM!@\r", "!x!(google)ru!\f", "Z$%^", "@COM"})
    void test_emailValidator_withInvalidEmail(String email) {
        assertThrows(BadInputParameters.class, () -> userService.validateUserForm(new UserForm(email, password)));
    }
}
