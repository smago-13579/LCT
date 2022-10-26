package com.example.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record AuthRequest(
        @NotNull @Email String email, @NotBlank String password
) {}
