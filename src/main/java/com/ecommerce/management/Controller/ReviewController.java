package com.ecommerce.management.Controller;

import com.ecommerce.management.Model.ReviewModel;
import com.ecommerce.management.Service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // POST /api/reviews
    @PostMapping
    public ResponseEntity<Map<String, Object>> addReview(@RequestBody Map<String, Object> body) {
        Long    productId = Long.valueOf(body.get("productId").toString());
        Long    userId    = Long.valueOf(body.get("userId").toString());
        Integer rating    = Integer.valueOf(body.get("rating").toString());
        String  comment   = body.getOrDefault("comment", "").toString();
        return ResponseEntity.ok(reviewService.addReview(productId, userId, rating, comment));
    }

    // GET /api/reviews/{productId}
    @GetMapping("/{productId}")
    public ResponseEntity<List<ReviewModel>> getReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }

    // GET /api/reviews/{productId}/rating
    @GetMapping("/{productId}/rating")
    public ResponseEntity<Map<String, Object>> getRating(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductRating(productId));
    }

    // DELETE /api/reviews/{reviewId}?userId=xxx
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Map<String, Object>> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(reviewService.deleteReview(reviewId, userId));
    }
}
