package com.project.shop_api.infrastructure.persistence.repository.impl;

import com.project.shop_api.domain.model.Customer;
import com.project.shop_api.domain.repository.CustomerRepository;
import com.project.shop_api.infrastructure.mapper.entity.CustomerEntityMapper;
import com.project.shop_api.infrastructure.persistence.entity.CustomerEntity;
import com.project.shop_api.infrastructure.persistence.repository.JpaCustomerRepository;
import com.project.shop_api.infrastructure.persistence.specification.CustomerSpecification;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final JpaCustomerRepository jpa;
    private final CustomerEntityMapper mapper;

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = mapper.toEntity(customer);
        CustomerEntity saved = jpa.save(entity);
        return mapper.toModel(saved);
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return jpa.findById(id).map(mapper::toModel);
    }

    @Override
    public Page<Customer> findAll(String email, int page, int size) {

        PageRequest pageable = PageRequest.of(page, size);

        Specification<CustomerEntity> spec = Specification.where(null);

        if (email != null && !email.isBlank()) {
            spec = spec.and(CustomerSpecification.emailContains(email));
        }

        return jpa.findAll(spec, pageable)
                  .map(mapper::toModel);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsEmail(String email) {
        return jpa.existsByEmail(email);
    }
}