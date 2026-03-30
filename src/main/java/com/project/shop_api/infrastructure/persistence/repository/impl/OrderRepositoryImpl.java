package com.project.shop_api.infrastructure.persistence.repository.impl;

import com.project.shop_api.domain.model.Order;
import com.project.shop_api.domain.repository.OrderRepository;
import com.project.shop_api.infrastructure.mapper.entity.OrderEntityMapper;
import com.project.shop_api.infrastructure.persistence.entity.*;
import com.project.shop_api.infrastructure.persistence.repository.*;
import com.project.shop_api.infrastructure.persistence.specification.OrderSpecification;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository orderJpa;
    private final JpaCustomerRepository customerJpa;
    private final JpaAddressRepository addressJpa;
    private final JpaProductRepository productJpa;
    private final OrderEntityMapper orderMapper;

    @Override
    public Order save(Order order) {

        OrderEntity entity = orderMapper.toEntity(order);

        // ASIGN RELATIONS
        CustomerEntity customer = customerJpa.getReferenceById(order.getCustomerId());
        entity.setCustomer(customer);

        AddressEntity address = addressJpa.getReferenceById(order.getShippingAddressId());
        entity.setShippingAddress(address);

        // ITEMS → PRODUCT + ORDER
        if (entity.getItems() != null) {
            for (OrderItemEntity item : entity.getItems()) {

                ProductEntity product = productJpa.getReferenceById(item.getProduct().getId());
                item.setProduct(product);

                item.setOrder(entity); // bidirectional relation
            }
        }

        OrderEntity saved = orderJpa.save(entity);

        return orderMapper.toModel(saved);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJpa.findById(id).map(orderMapper::toModel);
    }

    @Override
    public Page<Order> findAll(
            Long customerId,
            String fromDate,
            String toDate,
            String status,
            int page, int size
    ) {

        Specification<OrderEntity> spec = Specification.where(null);

        if (customerId != null) {
            spec = spec.and(OrderSpecification.customerIdEquals(customerId));
        }

        if (fromDate != null) {
            spec = spec.and(OrderSpecification.fromDate(LocalDateTime.parse(fromDate)));
        }

        if (toDate != null) {
            spec = spec.and(OrderSpecification.toDate(LocalDateTime.parse(toDate)));
        }

        if (status != null) {
            spec = spec.and(OrderSpecification.statusEquals(status));
        }

        Page<OrderEntity> result = orderJpa.findAll(spec, PageRequest.of(page, size));

        return result.map(orderMapper::toModel);
    }

    @Override
    public void deleteById(Long id) {
        orderJpa.deleteById(id);
    }
}