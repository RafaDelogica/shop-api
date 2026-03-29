package com.project.shop_api.infrastructure.persistence.specification;

import org.springframework.data.jpa.domain.Specification;
import com.project.shop_api.infrastructure.persistence.entity.CustomerEntity;

public class CustomerSpecification {

    public static Specification<CustomerEntity> emailContains(String email) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }
}