package com.ecommerce.management.Repository;

import com.ecommerce.management.Model.UserModel;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    UserModel save(UserModel user);
    Optional<UserModel> findByEmail(String email);
    Optional<UserModel> findById(Long userId);
    List<UserModel> findAll();
    boolean existsByEmail(String email);
    boolean delete(Long userId);
    boolean updateProfile(Long userId, String name, String email, String phone);
    boolean updatePassword(Long userId, String newPasswordHash);
}