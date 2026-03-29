package com.project.shop_api.infrastructure.rest.controller;

import com.project.shop_api.application.service.CustomerService;
import com.project.shop_api.infrastructure.mapper.dto.CustomerDtoMapper;
import com.project.shop_api.infrastructure.mapper.dto.AddressDtoMapper;
import com.project.shop_api.infrastructure.rest.dto.request.CustomerRequest;
import com.project.shop_api.infrastructure.rest.dto.request.AddressRequest;
import com.project.shop_api.infrastructure.rest.dto.response.CustomerResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;
    private final CustomerDtoMapper customerMapper;
    private final AddressDtoMapper addressMapper;

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {

        log.info("POST /api/customers - Creating new customer");

        return ResponseEntity.ok(customerMapper.toResponse(service.create(customerMapper.toDomain(request))));
    }

    @GetMapping
    public Page<CustomerResponse> list(
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /api/customers - Listing customers");

        return service.findAll(email, page, size).map(customerMapper::toResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long id) {

        log.info("GET /api/customers/{} - Getting customer detail", id);

        return ResponseEntity.ok(customerMapper.toResponse(service.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {

        log.info("PUT /api/customers/{} - Updating customer", id);

        return ResponseEntity.ok(customerMapper.toResponse(service.update(id, customerMapper.toDomain(request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        log.info("DELETE /api/customers/{} - Removing customer", id);

        service.delete(id);
        
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<CustomerResponse> addAddress(@PathVariable Long id, @Valid @RequestBody AddressRequest request) {

        log.info("POST /api/customers/{}/addresses - Adding address", id);

        return ResponseEntity.ok(customerMapper.toResponse(service.addAddress(id, addressMapper.toDomain(request))));
    }

    @PutMapping("/{id}/addresses/{addressId}/default")
    public ResponseEntity<CustomerResponse> setDefaultAddress(@PathVariable Long id, @PathVariable Long addressId) {

        log.info("PUT /api/customers/{}/addresses/{}/default - Setting default address", id, addressId);

        return ResponseEntity.ok(customerMapper.toResponse(service.markDefault(id, addressId)));
    }
}