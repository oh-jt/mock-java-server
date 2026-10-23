package com.example.mock.user;

public record UserResponse(long id, long tenantId, String email, String displayName, String role, String createdAt) {
}
