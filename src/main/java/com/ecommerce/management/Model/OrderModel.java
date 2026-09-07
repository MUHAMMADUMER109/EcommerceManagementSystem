package com.ecommerce.management.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderModel {

    @JsonProperty("orderId")
    private Long orderId;

    private Long userId;
    private String customerName;
    private String customerEmail;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private String productName;
    private String productCategory;
    private Integer quantity;
    private Double totalAmount;
    private LocalDateTime orderDate;
    private String status;
}