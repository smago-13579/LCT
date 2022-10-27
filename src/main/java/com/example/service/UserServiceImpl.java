package com.example.service;

import com.example.dto.AuthRequest;
import com.example.dto.UserDto;
import com.example.entity.UserDAO;
import com.example.entity.util.AccountStatus;
import com.example.entity.util.UserInfo;
import com.example.models.*;
import com.example.repository.UsersRepository;
import com.example.security.JwtTokenUtil;
import com.example.service.util.Validator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserServiceImpl implements UserService {
    private final AuthenticationManager authenticationManager;
    private final UsersRepository usersRepository;
    private final JwtUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final Validator validator;
    private final MailSender mailSender;

    public UserServiceImpl(AuthenticationManager authenticationManager,
                           UsersRepository usersRepository,
                           JwtUserDetailsService userDetailsService,
                           PasswordEncoder passwordEncoder,
                           JwtTokenUtil jwtTokenUtil,
                           Validator validator,
                           MailSender mailSender)
    {
        this.authenticationManager = authenticationManager;
        this.usersRepository = usersRepository;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.validator = validator;
        this.mailSender = mailSender;
    }

    @Override
    @Transactional
    public void registerUser(UserDto userDto) {
        validator.validateUserForm(userDto.getEmail(), userDto.getPassword());
        LocalDate date = validator.validateDate(userDto.getBirthDate());
        Integer code = ThreadLocalRandom.current().nextInt(100000, 999999);
        try {
            usersRepository.save(
                    UserDAO.builder()
                            .email(userDto.getEmail())
                            .password(passwordEncoder.encode(userDto.getPassword()))
                            .userInfo(new UserInfo(userDto.getFirstName(), userDto.getLastName(),
                                    userDto.getMiddleName(), userDto.getTelephone(), date, code))
                            .status(AccountStatus.NOT_ACTIVE)
                            .build()
            );
            SimpleMailMessage simpleMail = new SimpleMailMessage();
            simpleMail.setFrom("noreply@gmail.com");
            simpleMail.setTo(userDto.getEmail());
            simpleMail.setSubject("Confirm email");
            simpleMail.setText("Verification code: " + code);
            mailSender.send(simpleMail);
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

    @Override
    @Transactional
    public void verifyUser(String id, String code) {
        try {
            Long userId = Long.parseLong(id);
            Integer userCode = Integer.parseInt(code);
            UserDAO user = usersRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

            if (user.getStatus() == AccountStatus.ACTIVE) {
                return;
            }

            if (user.getStatus() != AccountStatus.NOT_ACTIVE) {
                throw new AccountBlockException("Account can't be activated");
            }

            if (!Objects.equals(user.getUserInfo().getCode(), userCode)) {
                throw new VerificationFailException("Verification fail for user id: " + id);
            }
            user.setStatus(AccountStatus.ACTIVE);
            usersRepository.save(user);
        } catch (NumberFormatException e) {
            throw new BadInputParameters("Invalid parameters: " + e.getMessage());
        }

    }

    public void validateUserForm(AuthRequest user) {
        validator.validateUserForm(user.email(), user.password());
    }
}
