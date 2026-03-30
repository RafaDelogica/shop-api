package com.project.shop_api.infrastructure.rest.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.project.shop_api.domain.enums.OrderStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {

    private Long id;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Long customerId;
    private Long shippingAddressId;
    private BigDecimal total;
    private List<OrderItemResponse> items;
}
