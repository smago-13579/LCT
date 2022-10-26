package com.example.repository;

import com.example.entity.UserDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends CrudRepository<UserDAO, Long> {
    Optional<UserDAO> findFirstByEmail(String email);
}
