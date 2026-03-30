package com.project.shop_api.application.service;

import com.project.shop_api.domain.model.Order;
import com.project.shop_api.domain.enums.OrderStatus;

import org.springframework.data.domain.Page;

public interface OrderService {

    Order create(Order order);

    Page<Order> findAll(
            Long customerId,
            String fromDate,
            String toDate,
            String status,
            int page,
            int size
    );

    Order findById(Long id);

    Order updateStatus(Long id, OrderStatus status);
}