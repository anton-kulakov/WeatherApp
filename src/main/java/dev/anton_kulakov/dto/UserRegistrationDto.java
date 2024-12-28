package dev.anton_kulakov.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDto {
    @NotEmpty(message = "Login should not be empty")
    @Size(min = 2, max = 20, message = "Login should be between 2 and 20 characters")
    private String login;

    @NotEmpty(message = "Password should not be empty")
    @Size(min = 5, max = 5, message = "Password should be exactly 5 characters long")
    private String password;

    @NotEmpty(message = "Password confirmation should not be empty")
    @Size(min = 5, max = 5, message = "Password should be exactly 5 characters long")
    private String confirmPassword;
}