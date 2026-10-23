package com.example.mock.user;

import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<UserResponse> findByIdAndTenant(long userId, long tenantId) {
        return jdbc.query(
                "SELECT id, tenant_id, email, display_name, role, created_at FROM users WHERE id = ? AND tenant_id = ?",
                (rs, i) -> new UserResponse(
                        rs.getLong("id"),
                        rs.getLong("tenant_id"),
                        rs.getString("email"),
                        rs.getString("display_name"),
                        rs.getString("role"),
                        rs.getString("created_at")),
                userId, tenantId).stream().findFirst();
    }
}
