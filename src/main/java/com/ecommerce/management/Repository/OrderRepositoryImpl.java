package com.ecommerce.management.Repository;

import com.ecommerce.management.Model.OrderModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<OrderModel> orderMapper = (rs, rowNum) -> {
        OrderModel o = new OrderModel();
        o.setOrderId(rs.getLong("order_id"));
        o.setUserId(rs.getLong("user_id"));
        o.setProductName(rs.getString("product_name"));
        try { o.setProductCategory(rs.getString("product_category")); } catch (Exception ignored) {}
        o.setQuantity(rs.getInt("quantity"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setOrderDate(rs.getTimestamp("order_date") != null ?
                rs.getTimestamp("order_date").toLocalDateTime() : null);
        try { o.setStatus(rs.getString("status")); } catch (Exception ignored) { o.setStatus("Confirmed"); }
        return o;
    };

    private final RowMapper<OrderModel> adminOrderMapper = (rs, rowNum) -> {
        OrderModel o = new OrderModel();
        o.setOrderId(rs.getLong("order_id"));
        o.setCustomerName(rs.getString("customer_name"));
        try { o.setCustomerEmail(rs.getString("customer_email")); } catch (Exception ignored) {}
        o.setProductName(rs.getString("product_name"));
        try { o.setProductCategory(rs.getString("product_category")); } catch (Exception ignored) {}
        o.setQuantity(rs.getInt("quantity"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setOrderDate(rs.getTimestamp("order_date") != null ?
                rs.getTimestamp("order_date").toLocalDateTime() : null);
        try { o.setStatus(rs.getString("status")); } catch (Exception ignored) { o.setStatus("Confirmed"); }
        return o;
    };

    @Override
    public Long placeOrder(Long userId, Long productId, Integer quantity, Double price,
                           String guestName, String guestEmail, String guestPhone) {
        String sql = "SELECT place_order(?, ?, ?, ?::numeric, ?, ?, ?)";
        return jdbcTemplate.queryForObject(sql, Long.class,
                userId, productId, quantity, price, guestName, guestEmail, guestPhone);
    }

    @Override
    public List<OrderModel> findByUserId(Long userId) {
        String sql = "SELECT o.order_id, o.user_id, p.name AS product_name, " +
                "p.category AS product_category, oi.quantity, oi.price AS unit_price, " +
                "o.total AS total_amount, o.order_date, o.status " +
                "FROM orders o " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "JOIN products p ON oi.product_id = p.product_id " +
                "WHERE o.user_id = ? " +
                "ORDER BY o.order_date DESC";
        return jdbcTemplate.query(sql, orderMapper, userId);
    }

    @Override
    public List<OrderModel> findAll() {
        String sql = "SELECT o.order_id, " +
                "COALESCE(u.name,  o.guest_name)  AS customer_name, " +
                "COALESCE(u.email, o.guest_email) AS customer_email, " +
                "p.name AS product_name, p.category AS product_category, " +
                "oi.quantity, oi.price AS unit_price, o.total AS total_amount, " +
                "o.order_date, o.status " +
                "FROM orders o " +
                "LEFT JOIN users u ON o.user_id = u.user_id " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "JOIN products p ON oi.product_id = p.product_id " +
                "ORDER BY o.order_date DESC";
        return jdbcTemplate.query(sql, adminOrderMapper);
    }

    @Override
    public boolean cancelOrder(Long orderId) {
        String restoreStock =
                "UPDATE products p SET stock = stock + oi.quantity " +
                "FROM order_items oi " +
                "WHERE oi.order_id = ? AND p.product_id = oi.product_id";
        jdbcTemplate.update(restoreStock, orderId);
        int rows = jdbcTemplate.update("DELETE FROM orders WHERE order_id = ?", orderId);
        return rows > 0;
    }

    @Override
    public boolean deleteOrder(Long orderId) {
        String restoreStock =
                "UPDATE products p SET stock = stock + oi.quantity " +
                "FROM order_items oi " +
                "WHERE oi.order_id = ? AND p.product_id = oi.product_id";
        jdbcTemplate.update(restoreStock, orderId);
        int rows = jdbcTemplate.update("DELETE FROM orders WHERE order_id = ?", orderId);
        return rows > 0;
    }

    @Override
    public boolean updateStatus(Long orderId, String status) {
        int rows = jdbcTemplate.update(
                "UPDATE orders SET status = ? WHERE order_id = ?", status, orderId);
        return rows > 0;
    }
}
