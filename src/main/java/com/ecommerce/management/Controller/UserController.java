package com.ecommerce.management.Controller;

import com.ecommerce.management.Model.UserModel;
import com.ecommerce.management.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // POST /api/users/register
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String name     = body.get("name");
        String email    = body.get("email");
        String password = body.get("password");
        String role     = body.getOrDefault("role", "CUSTOMER");
        String phone    = body.getOrDefault("phone", null);

        if (name == null || email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "All fields required."));
        }

        return ResponseEntity.ok(userService.register(name, email, password, role, phone));
    }

    // POST /api/users/login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String email    = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email and password required."));
        }

        return ResponseEntity.ok(userService.login(email, password));
    }
    // GET /api/users/all — admin only
    @GetMapping("/all")
    public ResponseEntity<List<UserModel>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // DELETE /api/users/{id} — admin delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    // PUT /api/users/profile/{id} — update name/email/phone
    @PutMapping("/profile/{id}")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String name  = body.get("name");
        String email = body.get("email");
        String phone = body.getOrDefault("phone", null);
        if (name == null || email == null || name.isBlank() || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Name and email are required."));
        }
        return ResponseEntity.ok(userService.updateProfile(id, name, email, phone));
    }

    // POST /api/users/password/{id} — change password
    @PostMapping("/password/{id}")
    public ResponseEntity<Map<String, Object>> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String current = body.get("currentPassword");
        String newPass = body.get("newPassword");
        if (current == null || newPass == null || newPass.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Passwords required. New password must be at least 6 characters."));
        }
        return ResponseEntity.ok(userService.changePassword(id, current, newPass));
    }

    @GetMapping("/generate-hash")
    public ResponseEntity<String> generateHash(@RequestParam String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return ResponseEntity.ok(encoder.encode(password));
    }
}

