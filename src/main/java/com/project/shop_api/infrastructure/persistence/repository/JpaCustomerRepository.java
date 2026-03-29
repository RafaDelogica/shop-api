package com.project.shop_api.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.project.shop_api.infrastructure.persistence.entity.CustomerEntity;

public interface JpaCustomerRepository extends
        JpaRepository<CustomerEntity, Long>,
        JpaSpecificationExecutor<CustomerEntity> {

	//Unique email per Customer
    boolean existsByEmail(String email);
}