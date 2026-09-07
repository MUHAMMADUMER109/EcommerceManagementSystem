package com.ecommerce.management.Service;

import com.ecommerce.management.Model.ReviewModel;
import com.ecommerce.management.Repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Map<String, Object> addReview(Long productId, Long userId, Integer rating, String comment) {
        Map<String, Object> response = new HashMap<>();
        if (rating < 1 || rating > 5) {
            response.put("success", false);
            response.put("message", "Rating must be between 1 and 5.");
            return response;
        }
        ReviewModel review = new ReviewModel();
        review.setProductId(productId);
        review.setUserId(userId);
        review.setRating(rating);
        review.setComment(comment != null ? comment : "");
        reviewRepository.save(review);
        response.put("success", true);
        response.put("message", "Review submitted successfully.");
        return response;
    }

    public List<ReviewModel> getProductReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    public Map<String, Object> getProductRating(Long productId) {
        Map<String, Object> result = new HashMap<>();
        double avg = reviewRepository.getAverageRating(productId);
        long count = reviewRepository.getReviewCount(productId);
        result.put("average", Math.round(avg * 10.0) / 10.0);
        result.put("count", count);
        return result;
    }

    public Map<String, Object> deleteReview(Long reviewId, Long userId) {
        Map<String, Object> response = new HashMap<>();
        boolean deleted = reviewRepository.delete(reviewId, userId);
        response.put("success", deleted);
        response.put("message", deleted ? "Review deleted." : "Review not found.");
        return response;
    }
}
