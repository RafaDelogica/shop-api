package com.project.shop_api.infrastructure.persistence.specification;

import org.springframework.data.jpa.domain.Specification;

import com.project.shop_api.infrastructure.persistence.entity.ProductEntity;

public class ProductSpecification {

	public static Specification<ProductEntity> nameContains(String name) {
		return (root, query, cb) -> 
				cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
	}
	
	public static Specification<ProductEntity> activeIs(Boolean active) {
		return (root, query, cb) ->
				cb.equal(root.get("active"), active);
	}
}
