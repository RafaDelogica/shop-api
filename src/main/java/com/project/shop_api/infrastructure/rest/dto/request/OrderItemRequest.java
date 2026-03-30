package com.project.shop_api.infrastructure.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderItemRequest {

    @NotNull
    private Long productId;

    @NotNull
    @Positive
    private Integer quantity;
}
