package com.develop.management.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @NotEmpty(message = "Email is required")
        @Size(min = 6, max = 50)
        @Email
        String email,
        @NotEmpty(message = "Password is required")
        @Size(min = 8, max = 100)
        String password
) {
}
