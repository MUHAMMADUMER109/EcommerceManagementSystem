package com.ecommerce.management.Service;

import com.ecommerce.management.Decorator.DiscountDecorator;
import com.ecommerce.management.Model.ProductModel;
import com.ecommerce.management.Repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductModel> getAllProducts() {
        return productRepository.findAll(); // NO forEach, NO decorator here
    }

    public List<ProductModel> searchProducts(String name) {
        return productRepository.findByName(name);
    }

    public Optional<ProductModel> getProductById(Long productId) {
        return productRepository.findById(productId);
    }

    public Map<String, Object> addProduct(String name, String category,
                                          Double price, Integer stock,
                                          String description, String imageUrl) {
        Map<String, Object> response = new HashMap<>();
        ProductModel product = new ProductModel();
        product.setName(name);
        product.setCategory(category);
        product.setPrice(price);
        product.setStock(stock);
        product.setDescription(description);
        product.setImageUrl(imageUrl);  // ← add this

        ProductModel saved = productRepository.save(product);
        response.put("success", true);
        response.put("message", "Product added.");
        response.put("productId", saved.getProductId());
        return response;
    }

    public Map<String, Object> updateProduct(Long productId,
                                             Double price, Integer stock) {
        Map<String, Object> response = new HashMap<>();
        boolean updated = productRepository.update(productId, price, stock);
        response.put("success", updated);
        response.put("message", updated ? "Product updated." : "Product not found.");
        return response;
    }

    public Map<String, Object> updateProductFull(Long productId, String name, String category,
                                                  Double price, Integer stock, String description) {
        Map<String, Object> response = new HashMap<>();
        boolean updated = productRepository.updateFull(productId, name, category, price, stock,
                description == null ? "" : description);
        response.put("success", updated);
        response.put("message", updated ? "Product updated." : "Product not found.");
        return response;
    }

    public Map<String, Object> deleteProduct(Long productId) {
        Map<String, Object> response = new HashMap<>();
        boolean deleted = productRepository.delete(productId);
        response.put("success", deleted);
        response.put("message", deleted ? "Product deleted." : "Product not found.");
        return response;
    }

    public Map<String, Object> applyDiscount(Long productId, Double discountPercent) {
        Map<String, Object> response = new HashMap<>();

        Optional<ProductModel> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Product not found.");
            return response;
        }

        // ===== DECORATOR PATTERN — apply custom discount =====
        ProductModel product = productOpt.get();
        ProductModel discounted = DiscountDecorator.applyDiscount(product, discountPercent);
        Double newPrice = discounted.getDiscountedPrice();

        boolean saved = productRepository.applyDiscount(productId, newPrice);
        response.put("success", saved);
        response.put("message", saved ? "Discount applied." : "Failed to apply discount.");
        response.put("discountedPrice", newPrice);
        response.put("originalPrice", product.getPrice());
        return response;
    }

    public Map<String, Object> removeDiscount(Long productId) {
        Map<String, Object> response = new HashMap<>();
        boolean removed = productRepository.removeDiscount(productId);
        response.put("success", removed);
        response.put("message", removed ? "Discount removed." : "Product not found.");
        return response;
    }

    public List<ProductModel> getLowStockProducts() {
        return productRepository.findLowStock(10);
    }
}