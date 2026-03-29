package com.project.shop_api.domain.repository;

import java.util.Optional;

import com.project.shop_api.domain.model.Address;

public interface AddressRepository {

	Address save(Address address);

	Optional<Address> findById(Long id);
}
