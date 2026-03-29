package com.project.shop_api.domain.repository;

import com.project.shop_api.domain.model.Customer;

import org.springframework.data.domain.Page;

import java.util.Optional;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(Long id);

    Page<Customer> findAll(String email, int page, int size);

    void deleteById(Long id);

    boolean existsEmail(String email);
}