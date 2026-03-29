package com.project.shop_api.infrastructure.mapper.dto;

import org.mapstruct.Mapper;

import com.project.shop_api.domain.model.Address;
import com.project.shop_api.infrastructure.rest.dto.request.AddressRequest;
import com.project.shop_api.infrastructure.rest.dto.response.AddressResponse;

@Mapper(componentModel = "spring")
public interface AddressDtoMapper {

    Address toDomain(AddressRequest request);

    AddressResponse toResponse(Address domain);
}