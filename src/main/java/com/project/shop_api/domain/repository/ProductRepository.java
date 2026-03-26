package com.project.shop_api.domain.repository;

import java.util.List;
import java.util.Optional;

import com.project.shop_api.domain.model.Product;

public interface ProductRepository {

	Optional<Product> findById(Long id);
	
	List<Product> findByAll();
	
	Product save(Product product);
	
	void deleteById(Long id);
}
