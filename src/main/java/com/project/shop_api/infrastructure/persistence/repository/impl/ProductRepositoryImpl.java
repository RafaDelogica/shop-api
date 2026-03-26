package com.project.shop_api.infrastructure.persistence.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.project.shop_api.domain.model.Product;
import com.project.shop_api.domain.repository.ProductRepository;
import com.project.shop_api.infrastructure.persistence.entity.ProductEntity;
import com.project.shop_api.infrastructure.mapper.entity.ProductEntityMapper;
import com.project.shop_api.infrastructure.persistence.repository.JpaProductRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository{
	
	private final JpaProductRepository jpaRepository;
	private final ProductEntityMapper entityMapper;

	@Override
	public Optional<Product> findById(Long id) {
		return jpaRepository.findById(id).map(entityMapper::toDomain);
	}

	@Override
	public List<Product> findByAll() {
		return jpaRepository.findAll()
				.stream()
				.map(entityMapper::toDomain)
				.toList();
	}

	@Override
	public Product save(Product product) {
		ProductEntity entity = entityMapper.toEntity(product);
		ProductEntity saved = jpaRepository.save(entity);
		return entityMapper.toDomain(saved);
	}

	@Override
	public void deleteById(Long id) {
		jpaRepository.deleteById(id);
	}

}
