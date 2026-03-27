package com.project.shop_api.application.service;

import org.springframework.data.domain.Page;

import com.project.shop_api.domain.model.Product;

public interface ProductService {

	Product create(Product product);
	
	Product update(Long id, Product product);
	
	Product findById(Long id);
	
	Page<Product> findAll(String name, Boolean active, int page, int size, String sort);
	
	void delete(Long id);
}
