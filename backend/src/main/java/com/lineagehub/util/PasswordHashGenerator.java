package com.lineagehub.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Utility class để generate BCrypt password hash
 * Dùng để test hoặc generate hash cho password
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        
        String password = "Admin@123";
        String hashedPassword = passwordEncoder.encode(password);
        
        System.out.println("====================================");
        System.out.println("Password Generator");
        System.out.println("====================================");
        System.out.println("Plain Password: " + password);
        System.out.println("Hashed Password: " + hashedPassword);
        System.out.println("====================================");
        
        // Verify
        boolean matches = passwordEncoder.matches(password, hashedPassword);
        System.out.println("Verification: " + (matches ? "✅ MATCHED" : "❌ NOT MATCHED"));
        System.out.println("====================================");
    }
}
