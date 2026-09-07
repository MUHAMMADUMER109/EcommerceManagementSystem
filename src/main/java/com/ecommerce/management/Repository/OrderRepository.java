package com.ecommerce.management.Repository;

import com.ecommerce.management.Model.OrderModel;
import java.util.List;

public interface OrderRepository {
    Long placeOrder(Long userId, Long productId, Integer quantity, Double price,
                    String guestName, String guestEmail, String guestPhone);
    List<OrderModel> findByUserId(Long userId);
    List<OrderModel> findAll();
    boolean cancelOrder(Long orderId);
    boolean deleteOrder(Long orderId);
    boolean updateStatus(Long orderId, String status);
}