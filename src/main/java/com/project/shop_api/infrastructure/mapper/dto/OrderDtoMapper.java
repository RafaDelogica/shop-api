package com.project.shop_api.infrastructure.mapper.dto;

import org.mapstruct.Mapper;

import com.project.shop_api.domain.model.Order;
import com.project.shop_api.infrastructure.rest.dto.request.OrderRequest;
import com.project.shop_api.infrastructure.rest.dto.response.OrderResponse;

@Mapper(componentModel = "spring", uses = { OrderItemDtoMapper.class })
public interface OrderDtoMapper {

    Order toDomain(OrderRequest request);

    OrderResponse toResponse(Order order);
}
