package com.expensetracker.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {
    
    private final Argon2 argon2;

    public PasswordEncoder() {
        this.argon2 = Argon2Factory.create();
    }

    public String encode(String rawPassword) {
        return argon2.hash(2, 65536, 1, rawPassword.toCharArray());
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        try {
            return argon2.verify(encodedPassword, rawPassword.toCharArray());
        } finally {
            argon2.wipeArray(rawPassword.toCharArray());
        }
    }
}