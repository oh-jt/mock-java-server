package com.example.mock.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Validated payload for creating a user. Role and tenant are never client-controlled. */
public record CreateUserRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(max = 100) String displayName,
        @NotBlank @Size(min = 12, max = 128) String password) {
}
