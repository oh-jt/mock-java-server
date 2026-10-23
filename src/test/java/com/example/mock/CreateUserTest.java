package com.example.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.mock.user.UserCreationHelper;

@SpringBootTest
@AutoConfigureMockMvc
class CreateUserTest {
    @Autowired MockMvc mvc;

    @Test
    void extractEmail() {
        assertEquals("bob@example.com", UserCreationHelper.extractEmail(Map.of("email", "bob@example.com")));
    }

    @Test
    void isValidEmail() {
        assertTrue(UserCreationHelper.isValidEmail("bob@example.com"));
    }

    @Test
    void protectPasswordReturnsValue() {
        assertEquals("secret", UserCreationHelper.protectPassword("secret"));
    }

    @Test
    void buildInsertSqlContainsTable() {
        String sql = UserCreationHelper.buildInsertSql(UserCreationHelper.insertColumns(),
                List.of("1", "a@b.c", "A", "pw", "member", "now"));
        assertTrue(sql.contains("INSERT INTO users"));
    }

    @Test
    void collectValuesOrder() {
        Map<String, Object> ctx = Map.of("tenantId", 1L, "email", "e", "displayName", "d",
                "password", "p", "role", "r", "createdAt", "t");
        assertEquals("e", UserCreationHelper.collectValues(ctx).get(1));
    }

    @Test
    void createUserHappyPath() throws Exception {
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"bob@example.com\",\"displayName\":\"Bob\",\"password\":\"hunter2-hunter2\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob@example.com"));
    }
}
