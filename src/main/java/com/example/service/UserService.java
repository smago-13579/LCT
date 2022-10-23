package com.example.service;

import com.example.dto.UserForm;
import com.example.entity.UserDAO;
import com.example.models.BadInputParameters;
import com.example.models.ConflictDataException;
import com.example.repository.UsersRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserService {
    private final UsersRepository usersRepository;

    public UserService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public void registerUser(UserForm userForm) {
        validateUserForm(userForm);
        try {
            usersRepository.save(new UserDAO(userForm.username(), userForm.password()));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictDataException(e.getMessage());
        }
    }

    public void validateUserForm(UserForm user) {
        if (StringUtils.isEmpty(user.username()) || StringUtils.isEmpty(user.password())) {
            throw new BadInputParameters("Invalid input");
        }

        if (!Pattern.compile("([^@\\s]+)@([^@\\s]+)$")
                .matcher(user.username())
                .matches()) {
            throw new BadInputParameters("invalid email");
        }
    }
}
