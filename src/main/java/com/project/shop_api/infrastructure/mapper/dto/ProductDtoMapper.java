package com.project.shop_api.infrastructure.mapper.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.project.shop_api.domain.model.Product;
import com.project.shop_api.infrastructure.rest.dto.request.ProductRequest;
import com.project.shop_api.infrastructure.rest.dto.response.ProductResponse;

@Mapper(componentModel = "spring")
public interface ProductDtoMapper {

    Product toDomain(ProductRequest request);

    ProductResponse toResponse(Product domain);
}