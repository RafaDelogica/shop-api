package com.project.shop_api.infrastructure.mapper.entity;

import org.mapstruct.Mapper;

import com.project.shop_api.domain.model.Customer;
import com.project.shop_api.infrastructure.persistence.entity.CustomerEntity;

@Mapper(componentModel = "spring", uses = { AddressEntityMapper.class })
public interface CustomerEntityMapper {

    CustomerEntity toEntity(Customer model);

    Customer toModel(CustomerEntity entity);
}