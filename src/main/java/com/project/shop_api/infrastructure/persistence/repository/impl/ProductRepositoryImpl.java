package com.project.shop_api.infrastructure.persistence.repository.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.project.shop_api.domain.model.Product;
import com.project.shop_api.domain.repository.ProductRepository;
import com.project.shop_api.infrastructure.persistence.entity.ProductEntity;
import com.project.shop_api.infrastructure.mapper.entity.ProductEntityMapper;
import com.project.shop_api.infrastructure.persistence.repository.JpaProductRepository;
import com.project.shop_api.infrastructure.persistence.specification.ProductSpecification;

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
	public Product save(Product product) {
		return entityMapper.toDomain(jpaRepository.save(entityMapper.toEntity(product)));
	}

	@Override
	public void deleteById(Long id) {
		jpaRepository.deleteById(id);
	}

	@Override
	public Page<Product> findAllPaged(String name, Boolean active, int page, int size, String sort) {

		String[] sortParts = sort.split(",");
		Sort.Direction direction = sortParts.length > 1 && 
				sortParts[1].equalsIgnoreCase("desc") 
				? Sort.Direction.DESC
				: Sort.Direction.ASC;
		
		Pageable pageable = PageRequest.of(page, size,
				Sort.by(new Sort.Order(direction, sortParts[0])));
		
		Specification<ProductEntity> spec = Specification.where(null);
		
		if (name != null) spec = spec.and(ProductSpecification.nameContains(name));
		if (active != null) spec = spec.and(ProductSpecification.activeIs(active));
		
		return jpaRepository.findAll(spec, pageable).map(entityMapper::toDomain);
	}

	@Override
	public boolean existsBySku(String sku) {
		return jpaRepository.existsBySku(sku);
	}

}
