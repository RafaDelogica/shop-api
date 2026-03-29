package com.project.shop_api.application.service;

import org.springframework.data.domain.Page;

import com.project.shop_api.domain.model.Address;
import com.project.shop_api.domain.model.Customer;

public interface CustomerService {

	    Customer create(Customer customer);

	    Customer update(Long id, Customer customer);

	    Customer findById(Long id);

	    Page<Customer> findAll(String email, int page, int size);

	    void delete(Long id);

	    Customer addAddress(Long customerId, Address address);

	    Customer markDefault(Long customerId, Long addressId);
}
