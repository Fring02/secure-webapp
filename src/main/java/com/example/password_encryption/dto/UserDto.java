package com.example.password_encryption.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    @NotNull
    @NotBlank(message = "Password or username are empty")
    @Size(min = 0, max = 20, message = "Username length should be between 0 and 20")
    private String username;
    @NotNull
    @NotBlank(message = "Password or username are empty")
    @Size(min = 7, message = "Password must contain at least 7 characters")
    private String password;
}
