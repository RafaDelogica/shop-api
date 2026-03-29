package com.project.shop_api.infrastructure.persistence.repository.impl;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.project.shop_api.domain.model.Address;
import com.project.shop_api.domain.repository.AddressRepository;
import com.project.shop_api.infrastructure.mapper.entity.AddressEntityMapper;
import com.project.shop_api.infrastructure.persistence.repository.JpaAddressRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AddressRepositoryImpl implements AddressRepository {

    private final JpaAddressRepository jpa;
    private final AddressEntityMapper mapper;

    @Override
    public Address save(Address address) {
        return mapper.toModel(jpa.save(mapper.toEntity(address)));
    }

    @Override
    public Optional<Address> findById(Long id) {
        return jpa.findById(id).map(mapper::toModel);
    }
}
