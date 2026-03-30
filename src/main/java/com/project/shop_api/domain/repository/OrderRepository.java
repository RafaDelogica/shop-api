package com.project.shop_api.domain.repository;

import com.project.shop_api.domain.model.Order;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    Page<Order> findAll(
            Long customerId,
            String fromDate,
            String toDate,
            String status,
            int page,
            int size
    );

    void deleteById(Long id);
}