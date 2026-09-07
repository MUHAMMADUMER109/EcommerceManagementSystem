package com.ecommerce.management.Controller;

import com.ecommerce.management.Model.OrderModel;
import com.ecommerce.management.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // POST /api/orders/place  — works for both logged-in users and guests
    @PostMapping("/place")
    public ResponseEntity<Map<String, Object>> placeOrder(
            @RequestBody Map<String, Object> body) {

        // userId is null for guest checkout
        Long userId = null;
        if (body.get("userId") != null) {
            try { userId = Long.valueOf(body.get("userId").toString()); }
            catch (NumberFormatException ignored) {}
        }

        String guestName  = body.getOrDefault("guestName",  "").toString();
        String guestEmail = body.getOrDefault("guestEmail", "").toString();
        String guestPhone = body.getOrDefault("guestPhone", "").toString();

        if (userId == null && (guestName.isBlank() || guestEmail.isBlank())) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Guest orders require name and email."));
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items =
                (List<Map<String, Object>>) body.get("items");

        if (items == null || items.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "No items in order."));
        }

        return ResponseEntity.ok(orderService.placeOrder(
                userId, items, guestName, guestEmail, guestPhone));
    }

    // GET /api/orders/history/{userId}
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<OrderModel>> getHistory(
            @PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrderHistory(userId));
    }

    // GET /api/orders/all
    @GetMapping("/all")
    public ResponseEntity<List<OrderModel>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // DELETE /api/orders/cancel/{orderId} — customer cancel
    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    // DELETE /api/orders/delete/{orderId} — admin delete
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<Map<String, Object>> deleteOrder(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.deleteOrder(orderId));
    }

    // PUT /api/orders/status/{orderId} — admin update status
    @PutMapping("/status/{orderId}")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Status required."));
        }
        return ResponseEntity.ok(orderService.updateStatus(orderId, status));
    }
}