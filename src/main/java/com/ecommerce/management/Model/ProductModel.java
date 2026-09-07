package com.ecommerce.management.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductModel {

    @JsonProperty("id")
    private Long productId;

    private String name;
    private Double price;
    private Integer stock;
    private String category;
    private String description;
    private String imageUrl;
    private Double discountedPrice;
}