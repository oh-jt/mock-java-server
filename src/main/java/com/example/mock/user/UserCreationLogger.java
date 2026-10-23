package com.example.mock.user;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Logging for the create-user endpoint. */
public class UserCreationLogger {
    private static final Logger log = LoggerFactory.getLogger(UserCreationLogger.class);

    /** Logs that a user is being created. */
    public static void logCreation(Map<String, Object> body) {
        log.info("Creating user from request: {}", body);
    }
}
