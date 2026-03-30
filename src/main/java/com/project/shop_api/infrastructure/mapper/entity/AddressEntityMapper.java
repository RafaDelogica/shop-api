package com.project.shop_api.infrastructure.mapper.entity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.project.shop_api.domain.model.Address;
import com.project.shop_api.infrastructure.persistence.entity.AddressEntity;

@Mapper(componentModel = "spring")
public interface AddressEntityMapper {

    @Mapping(target = "isDefault", expression = "java(model.getIsDefault() != null ? model.getIsDefault() : false)")
    @Mapping(target = "customer", ignore = true)
    AddressEntity toEntity(Address model);

    @Mapping(target = "isDefault", expression = "java(entity.isDefault())")
    Address toModel(AddressEntity entity);
}