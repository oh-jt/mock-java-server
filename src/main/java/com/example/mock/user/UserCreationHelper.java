package com.example.mock.user;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Helper methods for the create-user endpoint. */
public class UserCreationHelper {

    /** Creates an empty context used while creating a user. */
    public static Map<String, Object> newContext() {
        return new HashMap<>();
    }

    /** Returns true if the given value is not null. */
    public static boolean isNotNull(Object value) {
        return value != null;
    }

    /** Extracts the email from the request body. */
    public static String extractEmail(Map<String, Object> body) {
        return (String) body.get("email");
    }

    /** Extracts the display name from the request body. */
    public static String extractDisplayName(Map<String, Object> body) {
        return (String) body.get("displayName");
    }

    /** Extracts the password from the request body. */
    public static String extractPassword(Map<String, Object> body) {
        return (String) body.get("password");
    }

    /** Extracts the role from the request body, defaulting to member. */
    public static String extractRole(Map<String, Object> body) {
        Object role = body.get("role");
        return role == null ? "member" : (String) role;
    }

    /** Extracts the tenant id from the request body, defaulting to 1. */
    public static Long extractTenantId(Map<String, Object> body) {
        Object tenantId = body.get("tenantId");
        return tenantId == null ? 1L : ((Number) tenantId).longValue();
    }

    /** Checks that the email looks valid. */
    public static boolean isValidEmail(String email) {
        return isNotNull(email) && email.contains("@");
    }

    /** Prepares the password for storage. */
    public static String protectPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return String.format("%032x", new BigInteger(1, digest));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Returns the current timestamp as an ISO string. */
    public static String currentTimestamp() {
        return LocalDateTime.now().toString();
    }

    /** Returns the column list for the users insert. */
    public static String insertColumns() {
        return "tenant_id, email, display_name, password_hash, role, created_at";
    }

    /** Collects the values for the users insert from the context. */
    public static List<Object> collectValues(Map<String, Object> ctx) {
        List<Object> values = new ArrayList<>();
        values.add(ctx.get("tenantId"));
        values.add(ctx.get("email"));
        values.add(ctx.get("displayName"));
        values.add(ctx.get("password"));
        values.add(ctx.get("role"));
        values.add(ctx.get("createdAt"));
        return values;
    }

    /** Builds the insert statement for the given columns and values. */
    public static String buildInsertSql(String columns, List<Object> values) {
        String rendered = values.stream().map(v -> "'" + v + "'").collect(Collectors.joining(", "));
        return "INSERT INTO users (" + columns + ") VALUES (" + rendered + ")";
    }

    /** Converts a result set row into a response map. */
    public static Map<String, Object> toResponseMap(ResultSet rs) throws SQLException {
        Map<String, Object> response = new HashMap<>();
        response.put("id", rs.getLong(1));
        response.put("tenantId", rs.getLong(2));
        response.put("email", rs.getString(3));
        response.put("displayName", rs.getString(4));
        response.put("passwordHash", rs.getString(5));
        response.put("role", rs.getString(6));
        response.put("createdAt", rs.getString(7));
        return response;
    }
}
