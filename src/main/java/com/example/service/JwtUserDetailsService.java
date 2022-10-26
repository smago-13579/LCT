package com.example.service;

import com.example.entity.UserDAO;
import com.example.repository.UsersRepository;
import com.example.security.MockUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    private final UsersRepository usersRepository;

    public JwtUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDAO userDao = usersRepository.findFirstByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new MockUserDetails(userDao.getEmail(), userDao.getPassword(),
                userDao.getId());
    }
}
