package com.project.shop_api.infrastructure.rest.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {

    @NotNull
    private Long customerId;

    @NotNull
    private Long shippingAddressId;

    @NotEmpty
    private List<OrderItemRequest> items;
}
