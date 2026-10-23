package com.example.mock.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String hash(String plain) {
        return encoder.encode(plain);
    }

    public boolean verify(String plain, String hashed) {
        return encoder.matches(plain, hashed);
    }
}
