package com.project.shop_api.infrastructure.persistence.repository.impl;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.project.shop_api.domain.model.Address;
import com.project.shop_api.domain.repository.AddressRepository;
import com.project.shop_api.infrastructure.mapper.entity.AddressEntityMapper;
import com.project.shop_api.infrastructure.persistence.entity.CustomerEntity;
import com.project.shop_api.infrastructure.persistence.entity.AddressEntity;
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

    @Override
    public long countByCustomerId(Long customerId) {
        return jpa.countByCustomerId(customerId);
    }

    @Override
    public boolean existsByIdAndCustomerId(Long addressId, Long customerId) {
        return jpa.existsByIdAndCustomerId(addressId, customerId);
    }

    @Override
    public Address saveForCustomer(Long customerId, Address address) {

        AddressEntity entityAddress = mapper.toEntity(address);

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(customerId);
        entityAddress.setCustomer(customerEntity);

        AddressEntity saved = jpa.save(entityAddress);

        return mapper.toModel(saved);
    }

}
