package com.ecommerce.management.Repository;

import com.ecommerce.management.Model.ProductModel;
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
public class ProductRepositoryImpl implements ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ProductModel> productMapper = (rs, rowNum) -> {
        ProductModel p = new ProductModel();
        p.setProductId(rs.getLong("product_id"));
        p.setName(rs.getString("name"));
        p.setPrice(rs.getDouble("price"));
        p.setStock(rs.getInt("stock"));
        p.setCategory(rs.getString("category"));
        p.setDescription(rs.getString("description"));
        // ===== Read image_url =====
        p.setImageUrl(rs.getString("image_url"));
        // ===== Read discounted_price from DB =====
        double dp = rs.getDouble("discounted_price");
        if (!rs.wasNull() && dp > 0) {
            p.setDiscountedPrice(dp);
        } else {
            p.setDiscountedPrice(null); // explicitly null if not set
        }
        return p;
    };

    @Override
    public ProductModel save(ProductModel product) {
        String sql = "INSERT INTO products (name, price, stock, category, description, image_url) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setInt(3, product.getStock());
            ps.setString(4, product.getCategory());
            ps.setString(5, product.getDescription());
            ps.setString(6, product.getImageUrl());  // ← add this
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            product.setProductId(((Number) keyHolder.getKeys()
                    .get("product_id")).longValue());
        }
        return product;
    }

    @Override
    public Optional<ProductModel> findById(Long productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        List<ProductModel> list = jdbcTemplate.query(sql, productMapper, productId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<ProductModel> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM products ORDER BY product_id",
                productMapper);
    }

    @Override
    public List<ProductModel> findByName(String name) {
        String sql = "SELECT * FROM products WHERE name ILIKE ?";
        return jdbcTemplate.query(sql, productMapper, "%" + name + "%");
    }

    @Override
    public List<ProductModel> findLowStock(int threshold) {
        String sql = "SELECT * FROM products WHERE stock < ? ORDER BY stock ASC";
        return jdbcTemplate.query(sql, productMapper, threshold);
    }

    @Override
    public boolean update(Long productId, Double price, Integer stock) {
        String sql = "UPDATE products SET price = ?, stock = ? WHERE product_id = ?";
        return jdbcTemplate.update(sql, price, stock, productId) > 0;
    }

    @Override
    public boolean updateFull(Long productId, String name, String category,
                              Double price, Integer stock, String description) {
        String sql = "UPDATE products SET name = ?, category = ?, price = ?, stock = ?, description = ? WHERE product_id = ?";
        return jdbcTemplate.update(sql, name, category, price, stock, description, productId) > 0;
    }

    @Override
    public boolean delete(Long productId) {
        return jdbcTemplate.update(
                "DELETE FROM products WHERE product_id = ?", productId) > 0;
    }

    @Override
    public boolean applyDiscount(Long productId, Double discountedPrice) {
        // Store discounted price in discounted_price column
        String sql = "UPDATE products SET discounted_price = ? WHERE product_id = ?";
        return jdbcTemplate.update(sql, discountedPrice, productId) > 0;
    }

    @Override
    public boolean removeDiscount(Long productId) {
        String sql = "UPDATE products SET discounted_price = NULL WHERE product_id = ?";
        return jdbcTemplate.update(sql, productId) > 0;
    }
}