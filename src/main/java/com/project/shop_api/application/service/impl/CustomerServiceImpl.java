package com.project.shop_api.application.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.shop_api.application.common.exception.ConflictException;
import com.project.shop_api.application.common.exception.ForbiddenException;
import com.project.shop_api.application.common.exception.ResourceNotFoundException;
import com.project.shop_api.application.service.CustomerService;
import com.project.shop_api.domain.model.Address;
import com.project.shop_api.domain.model.Customer;
import com.project.shop_api.domain.repository.AddressRepository;
import com.project.shop_api.domain.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public Customer create(Customer customer) {

        log.info("Creating customer with email={}", customer.getEmail());

        if (customerRepository.existsEmail(customer.getEmail())) {
            throw new ConflictException("Email already exists: " + customer.getEmail());
        }

        if (customer.getAddresses() != null && !customer.getAddresses().isEmpty()) {
            boolean first = true;
            for (Address addr : customer.getAddresses()) {
                addr.setIsDefault(first);
                first = false;
            }
        }

        Customer saved = customerRepository.save(customer);
        
        log.info("Customer created id={}", saved.getId());
        return saved;
    }

    @Override
    public Customer update(Long id, Customer customer) {

        log.info("Updating customer id={}", id);

        Customer existing = findById(id);

        if (!existing.getEmail().equals(customer.getEmail())
                && customerRepository.existsEmail(customer.getEmail())) {
            throw new ConflictException("Email already exists: " + customer.getEmail());
        }

        customer.setId(id);
        customer.setAddresses(existing.getAddresses());
        customer.setCreatedAt(existing.getCreatedAt());
        customer.setUpdatedAt(existing.getUpdatedAt());

        Customer updated = customerRepository.save(customer);

        log.info("Customer updated id={}", id);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Customer findById(Long id) {

        log.info("Searching customer id={}", id);

        return customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Customer not found id={}", id);
                    return new ResourceNotFoundException("Customer not found with id " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Customer> findAll(String email, int page, int size) {

        log.info("Listing customers filtered by email='{}'", email);

        return customerRepository.findAll(email, page, size);
    }

    @Override
    public void delete(Long id) {

        log.info("Deleting customer id={}", id);

        Customer existing = findById(id); // lanza 404 si no existe

        customerRepository.deleteById(existing.getId());

        log.info("Customer deleted id={}", id);
    }

    @Override
    public Customer addAddress(Long customerId, Address address) {

        log.info("Adding address to customer id={}", customerId);

        Customer customer = findById(customerId);

        long count = addressRepository.countByCustomerId(customerId);
        address.setIsDefault(count == 0);

        Address addressToSave = Address.builder()
                .id(null)
                .line1(address.getLine1())
                .line2(address.getLine2())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(address.getIsDefault())
                .build();

        Address savedAddress = addressRepository.saveForCustomer(customerId, addressToSave);

        customer.getAddresses().add(savedAddress);

        return customerRepository.save(customer);
    }

    @Override
    public Customer markDefault(Long customerId, Long addressId) {

        log.info("Setting address default customerId={}, addressId={}", customerId, addressId);

        Customer customer = findById(customerId);

        if (!addressRepository.existsByIdAndCustomerId(addressId, customerId)) {
            log.warn("Address {} does not belong to customer {}", addressId, customerId);
            throw new ForbiddenException("The address does not belong to this customer");
        }

        customer.getAddresses()
                .forEach(addr -> addr.setIsDefault(addr.getId().equals(addressId)));

        Customer updated = customerRepository.save(customer);

        log.info("Default address set correctly for customer {}", customerId);
        return updated;
    }
}

