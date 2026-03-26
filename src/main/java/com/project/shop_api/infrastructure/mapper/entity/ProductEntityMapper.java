package com.project.shop_api.infrastructure.mapper.entity;

import org.mapstruct.Mapper;

import com.project.shop_api.domain.model.Product;
import com.project.shop_api.infrastructure.persistence.entity.ProductEntity;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {

	ProductEntity toEntity(Product domain);
	
	Product toDomain(ProductEntity entity);
}
