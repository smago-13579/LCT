package com.example.service;

import com.example.dto.AuthRequest;
import com.example.dto.UserDto;
import com.example.entity.UserDAO;
import com.example.entity.util.AccountStatus;
import com.example.entity.util.UserInfo;
import com.example.models.AuthOkResponse;
import com.example.models.BadInputParameters;
import com.example.models.ConflictDataException;
import com.example.repository.UsersRepository;
import com.example.security.JwtTokenUtil;
import com.example.service.util.Validator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
    private final AuthenticationManager authenticationManager;
    private final UsersRepository usersRepository;
    private final JwtUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final Validator validator;

    public UserServiceImpl(AuthenticationManager authenticationManager,
                           UsersRepository usersRepository,
                           JwtUserDetailsService userDetailsService,
                           PasswordEncoder passwordEncoder,
                           JwtTokenUtil jwtTokenUtil,
                           Validator validator)
    {
        this.authenticationManager = authenticationManager;
        this.usersRepository = usersRepository;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.validator = validator;
    }

    @Override
    public void registerUser(UserDto userDto) {
        validator.validateUserForm(userDto.getEmail(), userDto.getPassword());
        LocalDate date = validator.validateDate(userDto.getBirthDate());

        try {
            usersRepository.save(
                    UserDAO.builder()
                            .email(userDto.getEmail())
                            .password(passwordEncoder.encode(userDto.getPassword()))
                            .userInfo(new UserInfo(userDto.getFirstName(), userDto.getLastName(),
                                    userDto.getMiddleName(), userDto.getTelephone(), date))
                            .status(AccountStatus.NOT_ACTIVE)
                            .build()
            );
        } catch (DataIntegrityViolationException e) {
            throw new ConflictDataException(e.getMessage());
        }
    }

    @Override
    public AuthOkResponse authorize(AuthRequest authRequest) throws Exception {
        authenticate(authRequest);
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.email());
        String token = jwtTokenUtil.generateToken(userDetails);
        return new AuthOkResponse(token);
    }

    private void authenticate(AuthRequest user) throws Exception {
        validateUserForm(user);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.email(), user.password()));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    public void validateUserForm(AuthRequest user) {
        validator.validateUserForm(user.email(), user.password());
    }
}
