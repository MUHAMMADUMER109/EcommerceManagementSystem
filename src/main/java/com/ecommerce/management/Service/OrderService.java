package com.ecommerce.management.Service;

import com.ecommerce.management.Model.OrderModel;
import com.ecommerce.management.Model.ProductModel;
import com.ecommerce.management.Repository.OrderRepository;
import com.ecommerce.management.Repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderService {

    private final OrderRepository   orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository) {
        this.orderRepository   = orderRepository;
        this.productRepository = productRepository;
    }

    public Map<String, Object> placeOrder(Long userId,
                                          List<Map<String, Object>> items,
                                          String guestName, String guestEmail,
                                          String guestPhone) {
        Map<String, Object> response = new HashMap<>();
        List<Long> orderIds = new ArrayList<>();

        try {
            for (Map<String, Object> item : items) {

                Object productIdObj = item.get("productId");
                Object quantityObj  = item.get("quantity");
                Object priceObj     = item.get("price");

                if (productIdObj == null || quantityObj == null || priceObj == null) {
                    response.put("success", false);
                    response.put("message", "Invalid item — missing productId, quantity or price.");
                    return response;
                }

                Long    productId = Long.valueOf(productIdObj.toString());
                Integer quantity  = Integer.valueOf(quantityObj.toString());
                Double  price     = Double.valueOf(priceObj.toString());

                Optional<ProductModel> productOpt = productRepository.findById(productId);
                if (productOpt.isEmpty()) {
                    response.put("success", false);
                    response.put("message", "Product not found: " + productId);
                    return response;
                }

                ProductModel product = productOpt.get();
                if (product.getStock() < quantity) {
                    response.put("success", false);
                    response.put("message", "Insufficient stock for: " + product.getName());
                    return response;
                }

                Long orderId = orderRepository.placeOrder(
                        userId, productId, quantity, price,
                        guestName, guestEmail, guestPhone);
                orderIds.add(orderId);
            }

            response.put("success", true);
            response.put("message", "Order placed successfully!");
            response.put("orderIds", orderIds);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Order failed: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> cancelOrder(Long orderId) {
        Map<String, Object> response = new HashMap<>();
        boolean cancelled = orderRepository.cancelOrder(orderId);
        response.put("success", cancelled);
        response.put("message", cancelled ? "Order cancelled successfully." : "Order not found.");
        return response;
    }

    public Map<String, Object> deleteOrder(Long orderId) {
        Map<String, Object> response = new HashMap<>();
        boolean deleted = orderRepository.deleteOrder(orderId);
        response.put("success", deleted);
        response.put("message", deleted ? "Order deleted successfully." : "Order not found.");
        return response;
    }

    public List<OrderModel> getOrderHistory(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<OrderModel> getAllOrders() {
        return orderRepository.findAll();
    }

    public Map<String, Object> updateStatus(Long orderId, String status) {
        Map<String, Object> response = new HashMap<>();
        boolean updated = orderRepository.updateStatus(orderId, status);
        response.put("success", updated);
        response.put("message", updated ? "Status updated." : "Order not found.");
        return response;
    }
}