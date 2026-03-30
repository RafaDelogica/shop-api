package com.project.shop_api.infrastructure.persistence.specification;

import com.project.shop_api.domain.enums.OrderStatus;
import com.project.shop_api.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecification {

    public static Specification<OrderEntity> customerIdEquals(Long customerId) {
        return (root, query, cb) ->
                cb.equal(root.get("customer").get("id"), customerId);
    }

    public static Specification<OrderEntity> fromDate(LocalDateTime from) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("orderDate"), from);
    }

    public static Specification<OrderEntity> toDate(LocalDateTime to) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("orderDate"), to);
    }

    public static Specification<OrderEntity> statusEquals(String status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), OrderStatus.valueOf(status));
    }
}