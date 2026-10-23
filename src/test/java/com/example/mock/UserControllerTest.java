package com.example.mock;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import com.example.mock.security.PasswordService;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired PasswordService passwords;

    long aliceId;

    @BeforeEach
    void seed() {
        jdbc.update("DELETE FROM users");
        jdbc.update("INSERT INTO users (tenant_id, email, display_name, password_hash) VALUES (?, ?, ?, ?)",
                1, "alice@example.com", "Alice", passwords.hash("correct-horse-battery"));
        aliceId = jdbc.queryForObject("SELECT id FROM users WHERE email = 'alice@example.com'", Long.class);
    }

    @Test
    void returnsUserWithoutPassword() throws Exception {
        mvc.perform(get("/tenants/1/users/" + aliceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void unknownIdIs404() throws Exception {
        mvc.perform(get("/tenants/1/users/999999")).andExpect(status().isNotFound());
    }

    @Test
    void wrongTenantIs404() throws Exception {
        mvc.perform(get("/tenants/2/users/" + aliceId)).andExpect(status().isNotFound());
    }

    @Test
    void nonNumericIdIs400() throws Exception {
        mvc.perform(get("/tenants/1/users/abc")).andExpect(status().isBadRequest());
    }
}
