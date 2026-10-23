package com.example.mock.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserRepository users;

    public UserController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/tenants/{tenantId}/users/{userId}")
    public UserResponse getUser(@PathVariable long tenantId, @PathVariable long userId) {
        // Same answer for "no such user" and "user in another tenant": do not leak existence.
        UserResponse user = users.findByIdAndTenant(userId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        log.info("user_fetched tenant_id={} user_id={}", tenantId, userId);
        return user;
    }
}
