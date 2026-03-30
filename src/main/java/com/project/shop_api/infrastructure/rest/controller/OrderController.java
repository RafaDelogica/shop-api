package com.project.shop_api.infrastructure.rest.controller;

import com.project.shop_api.application.service.OrderService;
import com.project.shop_api.infrastructure.mapper.dto.OrderDtoMapper;
import com.project.shop_api.infrastructure.rest.dto.request.OrderRequest;
import com.project.shop_api.infrastructure.rest.dto.request.OrderStatusUpdateRequest;
import com.project.shop_api.infrastructure.rest.dto.response.OrderResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;
    private final OrderDtoMapper mapper;

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody OrderRequest request) {

        log.info("POST /api/orders - Creating order");

        return ResponseEntity.ok(mapper.toResponse(service.create(mapper.toDomain(request))));
    }

    @GetMapping
    public Page<OrderResponse> findAll(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /api/orders - Listing");

        return service.findAll(customerId, fromDate, toDate, status, page, size)
                .map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {

        log.info("GET /api/orders/{} - Detail", id);

        return ResponseEntity.ok(mapper.toResponse(service.findById(id)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {

        log.info("PUT /api/orders/{}/status - Changing status to {}", id, request.getStatus());

        return ResponseEntity.ok(mapper.toResponse(service.updateStatus(id, request.getStatus())));
    }
}