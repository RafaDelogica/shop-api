package com.project.shop_api.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.project.shop_api.domain.model.Product;

public interface ProductRepository {

	Optional<Product> findById(Long id);
	
	Page<Product> findAllPaged(String name, Boolean active,int page, int size, String sort);

	Product save(Product product);
	
	void deleteById(Long id);
	
	boolean existsBySku(String sku);
}
