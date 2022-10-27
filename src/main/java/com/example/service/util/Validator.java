package com.example.service.util;

import com.example.dto.AuthRequest;
import com.example.dto.UserDto;
import com.example.models.BadInputParameters;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

@Component
public class Validator {

    public void validateUserForm(String email, String password) {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
            throw new BadInputParameters("Invalid input");
        }

        if (!Pattern.compile("([^@\\s]+)@([^@\\s]+)$")
                .matcher(email)
                .matches()) {
            throw new BadInputParameters("invalid email");
        }
    }

    public LocalDate validateDate(String birthDate) {
        try {
            LocalDate date = LocalDate.parse(birthDate);
            LocalDate validDate = LocalDate.now().minusYears(18);

            if (date.isAfter(validDate)) {
                throw new BadInputParameters("The user must be at least 18 years old");
            }
            return date;
        } catch (DateTimeParseException e) {
            throw new BadInputParameters(e.getMessage());
        }
    }
}
