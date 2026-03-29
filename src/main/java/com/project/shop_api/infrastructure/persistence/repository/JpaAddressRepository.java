package com.project.shop_api.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.shop_api.infrastructure.persistence.entity.AddressEntity;

public interface JpaAddressRepository extends JpaRepository<AddressEntity, Long> {

	//To know if an address is the first one(by default) to the Customer 
    long countByCustomerId(Long customerId);
    
    //To check if the address actually belong to the customer
    boolean existsByIdAndCustomerId(Long addressId, Long customerId);
}