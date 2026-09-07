package com.ecommerce.management.Util;

import com.ecommerce.management.Model.UserModel;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean checkPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    public boolean isAdmin(UserModel user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }

    public boolean isCustomer(UserModel user) {
        return user != null && "CUSTOMER".equalsIgnoreCase(user.getRole());
    }
}