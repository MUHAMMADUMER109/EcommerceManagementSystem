package com.ecommerce.management.Repository;

import com.ecommerce.management.Model.ReviewModel;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    ReviewModel save(ReviewModel review);
    List<ReviewModel> findByProductId(Long productId);
    Optional<ReviewModel> findByProductAndUser(Long productId, Long userId);
    boolean delete(Long reviewId, Long userId);
    double getAverageRating(Long productId);
    long getReviewCount(Long productId);
}
