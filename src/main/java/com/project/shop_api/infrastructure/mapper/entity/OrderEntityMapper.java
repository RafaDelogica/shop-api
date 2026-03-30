package com.project.shop_api.infrastructure.mapper.entity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.project.shop_api.domain.model.Order;
import com.project.shop_api.infrastructure.persistence.entity.OrderEntity;

@Mapper(componentModel = "spring", uses = { OrderItemEntityMapper.class })
public interface OrderEntityMapper {

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "shippingAddressId", source = "shippingAddress.id")
    Order toModel(OrderEntity entity);

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "shippingAddress", ignore = true)
    OrderEntity toEntity(Order domain);
}
