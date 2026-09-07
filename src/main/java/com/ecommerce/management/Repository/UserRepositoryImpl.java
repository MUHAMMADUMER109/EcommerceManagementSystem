package com.ecommerce.management.Repository;

import com.ecommerce.management.Model.UserModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ===== ROW MAPPER =====
    private final RowMapper<UserModel> userMapper = (rs, rowNum) -> {
        UserModel user = new UserModel();
        user.setUserId(rs.getLong("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("created_at") != null ?
                rs.getTimestamp("created_at").toLocalDateTime() : null);
        try { user.setPhone(rs.getString("phone")); } catch (Exception ignored) {}
        return user;
    };

    @Override
    public UserModel save(UserModel user) {
        String sql = "INSERT INTO users (name, email, password, role, phone) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole() != null ? user.getRole() : "CUSTOMER");
            ps.setString(5, user.getPhone());
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            user.setUserId(((Number) keyHolder.getKeys().get("user_id")).longValue());
        }
        return user;
    }

    @Override
    public Optional<UserModel> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<UserModel> users = jdbcTemplate.query(sql, userMapper, email);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public Optional<UserModel> findById(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<UserModel> users = jdbcTemplate.query(sql, userMapper, userId);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public List<UserModel> findAll() {
        return jdbcTemplate.query("SELECT * FROM users ORDER BY created_at DESC", userMapper);
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean delete(Long userId) {
        int rows = jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", userId);
        return rows > 0;
    }

    @Override
    public boolean updateProfile(Long userId, String name, String email, String phone) {
        int rows = jdbcTemplate.update(
                "UPDATE users SET name = ?, email = ?, phone = ? WHERE user_id = ?",
                name, email, phone, userId);
        return rows > 0;
    }

    @Override
    public boolean updatePassword(Long userId, String newPasswordHash) {
        int rows = jdbcTemplate.update(
                "UPDATE users SET password = ? WHERE user_id = ?",
                newPasswordHash, userId);
        return rows > 0;
    }
}