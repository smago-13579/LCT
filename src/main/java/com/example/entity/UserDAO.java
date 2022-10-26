package com.example.entity;

import com.example.entity.util.AccountStatus;
import com.example.entity.util.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDAO {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Embedded
    private UserInfo userInfo;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    public UserDAO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
