package com.ecommerce.management.Repository;

import com.ecommerce.management.Model.ReviewModel;
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
public class ReviewRepositoryImpl implements ReviewRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReviewRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ReviewModel> reviewMapper = (rs, rowNum) -> {
        ReviewModel r = new ReviewModel();
        r.setReviewId(rs.getLong("review_id"));
        r.setProductId(rs.getLong("product_id"));
        r.setUserId(rs.getLong("user_id"));
        r.setRating(rs.getInt("rating"));
        r.setComment(rs.getString("comment"));
        r.setCreatedAt(rs.getTimestamp("created_at") != null ?
                rs.getTimestamp("created_at").toLocalDateTime() : null);
        try { r.setUserName(rs.getString("user_name")); } catch (Exception ignored) {}
        return r;
    };

    @Override
    public ReviewModel save(ReviewModel review) {
        String sql = "INSERT INTO reviews (product_id, user_id, rating, comment) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (product_id, user_id) DO UPDATE " +
                "SET rating = EXCLUDED.rating, comment = EXCLUDED.comment, created_at = NOW() " +
                "RETURNING review_id";
        Long id = jdbcTemplate.queryForObject(sql, Long.class,
                review.getProductId(), review.getUserId(), review.getRating(), review.getComment());
        review.setReviewId(id);
        return review;
    }

    @Override
    public List<ReviewModel> findByProductId(Long productId) {
        String sql = "SELECT r.*, u.name AS user_name FROM reviews r " +
                "JOIN users u ON r.user_id = u.user_id " +
                "WHERE r.product_id = ? ORDER BY r.created_at DESC";
        return jdbcTemplate.query(sql, reviewMapper, productId);
    }

    @Override
    public Optional<ReviewModel> findByProductAndUser(Long productId, Long userId) {
        String sql = "SELECT r.*, u.name AS user_name FROM reviews r " +
                "JOIN users u ON r.user_id = u.user_id " +
                "WHERE r.product_id = ? AND r.user_id = ?";
        List<ReviewModel> list = jdbcTemplate.query(sql, reviewMapper, productId, userId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public boolean delete(Long reviewId, Long userId) {
        int rows = jdbcTemplate.update(
                "DELETE FROM reviews WHERE review_id = ? AND user_id = ?", reviewId, userId);
        return rows > 0;
    }

    @Override
    public double getAverageRating(Long productId) {
        String sql = "SELECT COALESCE(AVG(rating::float), 0) FROM reviews WHERE product_id = ?";
        Double avg = jdbcTemplate.queryForObject(sql, Double.class, productId);
        return avg != null ? avg : 0.0;
    }

    @Override
    public long getReviewCount(Long productId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE product_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, productId);
        return count != null ? count : 0;
    }
}
