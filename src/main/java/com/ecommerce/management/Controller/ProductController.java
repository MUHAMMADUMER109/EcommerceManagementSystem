package com.ecommerce.management.Controller;

import com.ecommerce.management.Model.ProductModel;
import com.ecommerce.management.Service.ProductService;
import com.ecommerce.management.Util.SupabaseStorageUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService        productService;
    private final SupabaseStorageUtil   storageUtil;

    public ProductController(ProductService productService,
                             SupabaseStorageUtil storageUtil) {
        this.productService = productService;
        this.storageUtil    = storageUtil;
    }

    // GET /api/products
    @GetMapping
    public ResponseEntity<List<ProductModel>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // GET /api/products/search?q=shirt
    @GetMapping("/search")
    public ResponseEntity<List<ProductModel>> search(@RequestParam String q) {
        return ResponseEntity.ok(productService.searchProducts(q));
    }

    // GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/products/lowstock
    @GetMapping("/lowstock")
    public ResponseEntity<List<ProductModel>> getLowStock() {
        return ResponseEntity.ok(productService.getLowStockProducts());
    }
    // POST /api/products/add — with image upload
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addProduct(
            @RequestParam("name")        String name,
            @RequestParam("category")    String category,
            @RequestParam("price")       Double price,
            @RequestParam("stock")       Integer stock,
            @RequestParam(value="description", defaultValue="") String description,
            @RequestParam(value="image", required=false) MultipartFile image) {

        Map<String, Object> response = new HashMap<>();
        String imageUrl = null;

        try {
            // ===== Upload image to Supabase Storage if provided =====
            if (image != null && !image.isEmpty()) {
                imageUrl = storageUtil.uploadImage(image, name);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Image upload failed: " + e.getMessage());
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(
                productService.addProduct(name, category, price, stock, description, imageUrl));
    }

    // PUT /api/products/update/{id}
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Double  price = Double.valueOf(body.get("price").toString());
        Integer stock = Integer.valueOf(body.get("stock").toString());

        // If full edit fields are present, use updateFull
        if (body.containsKey("name")) {
            String name        = body.get("name").toString();
            String category    = body.getOrDefault("category", "").toString();
            String description = body.getOrDefault("description", "").toString();
            return ResponseEntity.ok(
                    productService.updateProductFull(id, name, category, price, stock, description));
        }

        return ResponseEntity.ok(productService.updateProduct(id, price, stock));
    }

    // DELETE /api/products/delete/{id}
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        productService.getProductById(id).ifPresent(p -> {
            if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
                try { storageUtil.deleteImage(p.getImageUrl()); }
                catch (Exception e) {
                    System.err.println("Warning: could not delete image from storage: " + e.getMessage());
                }
            }
        });
        return ResponseEntity.ok(productService.deleteProduct(id));
    }

    // POST /api/products/discount/{id}
    @PostMapping("/discount/{id}")
    public ResponseEntity<Map<String, Object>> applyDiscount(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Double discountPercent = Double.valueOf(
                body.get("discountPercent").toString());
        return ResponseEntity.ok(productService.applyDiscount(id, discountPercent));
    }

    // DELETE /api/products/discount/{id}
    @DeleteMapping("/discount/{id}")
    public ResponseEntity<Map<String, Object>> removeDiscount(@PathVariable Long id) {
        return ResponseEntity.ok(productService.removeDiscount(id));
    }
}