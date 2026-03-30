package com.project.shop_api.infrastructure.mapper.dto;

import org.mapstruct.Mapper;

import com.project.shop_api.domain.model.Customer;
import com.project.shop_api.infrastructure.rest.dto.request.CustomerRequest;
import com.project.shop_api.infrastructure.rest.dto.response.CustomerResponse;

@Mapper(componentModel = "spring", uses = { AddressDtoMapper.class })
public interface CustomerDtoMapper {

    Customer toDomain(CustomerRequest request);

    CustomerResponse toResponse(Customer domain);
}