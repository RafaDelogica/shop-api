package com.project.shop_api.application.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.project.shop_api.application.service.ProductService;
import com.project.shop_api.domain.model.Product;
import com.project.shop_api.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
	
	private final ProductRepository repository;

	@Override
	public Product create(Product product) {
		if(repository.existsBySku(product.getSku())) {
			throw new RuntimeException("Ya existe un producto con el SKU " + product.getSku());
		}
		
		return repository.save(product);
	}

	@Override
	public Product update(Long id, Product product) {
		
		Product existingProduct = findById(id);
		
		if(!existingProduct.getSku().equals(product.getSku()) && repository.existsBySku(product.getSku())){
			throw new RuntimeException("Ya existe un producto con el SKU " + product.getSku());
		}
		
		product.setId(id);
		return repository.save(product);
	}

	@Override
	public Product findById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Product not found " + id));
	}

	@Override
	public Page<Product> findAll(String name, Boolean active, int page, int size, String sort) {
		return repository.findAllPaged(name, active, page, size, sort);
	}

	@Override
	public void delete(Long id) {
		Product product = findById(id);
		product.setActive(false);
		repository.save(product);
	}

}
