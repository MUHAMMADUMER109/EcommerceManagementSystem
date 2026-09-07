package com.ecommerce.management.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    private Long userId;
    private String name;
    private String email;
    private String phone;

    @JsonIgnore
    private String password;

    private String role;
    private LocalDateTime createdAt;
}