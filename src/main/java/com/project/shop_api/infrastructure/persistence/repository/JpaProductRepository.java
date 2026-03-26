package com.project.shop_api.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.shop_api.infrastructure.persistence.entity.ProductEntity;

public interface JpaProductRepository extends JpaRepository<ProductEntity, Long>{

}
