package com.ecommerce.management.Service;

import com.ecommerce.management.Factory.UserFactory;
import com.ecommerce.management.Model.UserModel;
import com.ecommerce.management.Repository.UserRepository;
import com.ecommerce.management.Util.AuthUtil;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthUtil authUtil;
    // ===== BCrypt for password hashing =====
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, AuthUtil authUtil) {
        this.userRepository = userRepository;
        this.authUtil = authUtil;
    }

    public Map<String, Object> register(String name, String email,
                                        String password, String role, String phone) {
        Map<String, Object> response = new HashMap<>();

        if (userRepository.existsByEmail(email)) {
            response.put("success", false);
            response.put("message", "Email already registered.");
            return response;
        }

        // ===== Hash password before saving =====
        UserModel user = UserFactory.createUser(name, email,
                passwordEncoder.encode(password), role);
        user.setPhone(phone);
        UserModel saved = userRepository.save(user);

        response.put("success", true);
        response.put("message", "Account created successfully.");
        response.put("userId", saved.getUserId());
        return response;
    }

    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    public Map<String, Object> deleteUser(Long userId) {
        Map<String, Object> response = new HashMap<>();
        boolean deleted = userRepository.delete(userId);
        response.put("success", deleted);
        response.put("message", deleted ? "User deleted." : "User not found.");
        return response;
    }

    public Map<String, Object> updateProfile(Long userId, String name, String email, String phone) {
        Map<String, Object> response = new HashMap<>();
        Optional<UserModel> existing = userRepository.findByEmail(email);
        if (existing.isPresent() && !existing.get().getUserId().equals(userId)) {
            response.put("success", false);
            response.put("message", "Email already in use by another account.");
            return response;
        }
        boolean updated = userRepository.updateProfile(userId, name, email, phone);
        response.put("success", updated);
        if (updated) {
            response.put("message", "Profile updated.");
            response.put("name",  name);
            response.put("email", email);
            response.put("phone", phone);
        } else {
            response.put("message", "User not found.");
        }
        return response;
    }

    public Map<String, Object> changePassword(Long userId, String currentPassword, String newPassword) {
        Map<String, Object> response = new HashMap<>();
        Optional<UserModel> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "User not found.");
            return response;
        }
        UserModel user = userOpt.get();
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            response.put("success", false);
            response.put("message", "Current password is incorrect.");
            return response;
        }
        boolean updated = userRepository.updatePassword(userId, passwordEncoder.encode(newPassword));
        response.put("success", updated);
        response.put("message", updated ? "Password changed successfully." : "Update failed.");
        return response;
    }

    public Map<String, Object> login(String email, String password) {
        Map<String, Object> response = new HashMap<>();

        java.util.Optional<UserModel> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Email not found.");
            return response;
        }

        UserModel user = userOpt.get();

        // ===== BCrypt check — matches hashed password =====
        if (!passwordEncoder.matches(password, user.getPassword())) {
            response.put("success", false);
            response.put("message", "Incorrect password.");
            return response;
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id",    user.getUserId());
        userInfo.put("name",  user.getName());
        userInfo.put("email", user.getEmail());
        userInfo.put("role",  user.getRole());
        userInfo.put("phone", user.getPhone());

        response.put("success", true);
        response.put("user", userInfo);
        return response;
    }
}