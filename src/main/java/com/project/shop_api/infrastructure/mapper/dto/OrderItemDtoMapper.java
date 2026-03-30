package com.project.shop_api.infrastructure.mapper.dto;

import org.mapstruct.Mapper;

import com.project.shop_api.domain.model.OrderItem;
import com.project.shop_api.infrastructure.rest.dto.request.OrderItemRequest;
import com.project.shop_api.infrastructure.rest.dto.response.OrderItemResponse;

@Mapper(componentModel = "spring")
public interface OrderItemDtoMapper {

    OrderItem toDomain(OrderItemRequest request);

    OrderItemResponse toResponse(OrderItem item);
}

