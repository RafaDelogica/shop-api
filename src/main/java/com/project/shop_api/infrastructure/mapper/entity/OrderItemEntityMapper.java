package com.project.shop_api.infrastructure.mapper.entity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.project.shop_api.domain.model.OrderItem;
import com.project.shop_api.infrastructure.persistence.entity.OrderItemEntity;

@Mapper(componentModel = "spring")
public interface OrderItemEntityMapper {

    @Mapping(target = "order", ignore = true)
    OrderItemEntity toEntity(OrderItem domain);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name") 
    OrderItem toModel(OrderItemEntity entity);
}
