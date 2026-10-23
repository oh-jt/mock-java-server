package com.example.mock.user;

import static com.example.mock.user.UserCreationHelper.*;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserRepository users;
    private final JdbcTemplate jdbc;

    public UserController(UserRepository users, JdbcTemplate jdbc) {
        this.users = users;
        this.jdbc = jdbc;
    }

    @GetMapping("/tenants/{tenantId}/users/{userId}")
    public UserResponse getUser(@PathVariable long tenantId, @PathVariable long userId) {
        // Same answer for "no such user" and "user in another tenant": do not leak existence.
        UserResponse user = users.findByIdAndTenant(userId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        log.info("user_fetched tenant_id={} user_id={}", tenantId, userId);
        return user;
    }

    @PostMapping("/users")
    public Map<String, Object> createUser(@RequestBody Map<String, Object> body) throws Exception {
        Map<String, Object> ctx = newContext();
        UserCreationLogger.logCreation(body);

        ctx.put("email", extractEmail(body));
        ctx.put("displayName", extractDisplayName(body));
        ctx.put("role", extractRole(body));
        ctx.put("tenantId", extractTenantId(body));
        ctx.put("password", protectPassword(extractPassword(body)));
        ctx.put("createdAt", currentTimestamp());

        if (!isValidEmail((String) ctx.get("email"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email");
        }

        String sql = buildInsertSql(insertColumns(), collectValues(ctx));
        jdbc.update(sql);
        return jdbc.queryForObject(
                "SELECT id, tenant_id, email, display_name, password_hash, role, created_at "
                        + "FROM users WHERE tenant_id = ? AND email = ?",
                (rs, i) -> toResponseMap(rs),
                ctx.get("tenantId"), ctx.get("email"));
    }
}
