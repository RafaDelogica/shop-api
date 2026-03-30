package com.project.shop_api.domain.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private Long id;

    private Long productId;

    private String productName; // added to show the name of the Product

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal lineTotal;
}
