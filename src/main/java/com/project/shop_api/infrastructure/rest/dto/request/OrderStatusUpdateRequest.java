package com.project.shop_api.infrastructure.rest.dto.request;

import com.project.shop_api.domain.enums.OrderStatus;

import lombok.Data;

@Data
public class OrderStatusUpdateRequest {
	
    private OrderStatus status;
}