package com.ecommerce.management.Repository;

import com.ecommerce.management.Model.ProductModel;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    ProductModel save(ProductModel product);
    Optional<ProductModel> findById(Long productId);
    List<ProductModel> findAll();
    List<ProductModel> findByName(String name);
    List<ProductModel> findLowStock(int threshold);
    boolean update(Long productId, Double price, Integer stock);
    boolean updateFull(Long productId, String name, String category, Double price, Integer stock, String description);
    boolean delete(Long productId);
    boolean applyDiscount(Long productId, Double discountedPrice);
    boolean removeDiscount(Long productId);
}