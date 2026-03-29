package com.project.shop_api.infrastructure.mapper.entity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.project.shop_api.domain.model.Address;
import com.project.shop_api.infrastructure.persistence.entity.AddressEntity;

@Mapper(componentModel = "spring")
public interface AddressEntityMapper {

    @Mapping(target = "customer", ignore = true) // El service asignará el customer correcto
    AddressEntity toEntity(Address model);

    Address toModel(AddressEntity entity);
}