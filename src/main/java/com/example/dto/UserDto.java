package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotBlank @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String middleName;

    @Pattern(regexp = "\\+7\\d{3}-\\d{3}-\\d{2}-\\d{2}")
    private String telephone;

    public UserDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
