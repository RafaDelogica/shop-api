package com.project.shop_api.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import com.project.shop_api.domain.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long id;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Long customerId;
    private Long shippingAddressId;
    private BigDecimal total;
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
}