package com.project.shop_api.application.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.shop_api.application.service.ProductService;
import com.project.shop_api.domain.model.Product;
import com.project.shop_api.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
	
	private final ProductRepository repository;

	@Override
	public Product create(Product product) {
		
		log.info("Creating product with SKU: {}", product.getSku());
		
		if(repository.existsBySku(product.getSku())) {
			log.warn("Product with SKU duplicated", product.getSku());
			throw new RuntimeException("Ya existe un producto con el SKU " + product.getSku());
		}
		
		Product productCreated = repository.save(product);
		
		log.info("Product created with ID: {}", productCreated.getId());
		
		return productCreated;
	}

	@Override
	public Product update(Long id, Product product) {
		
		log.info("Updating product with ID: {}", id);
		
		Product existingProduct = findById(id);
		
		if(!existingProduct.getSku().equals(product.getSku()) && repository.existsBySku(product.getSku())){
			log.warn("Product with SKU duplicated", product.getSku());
			throw new RuntimeException("Ya existe un producto con el SKU " + product.getSku());
		}
		
		product.setId(id);
		Product productUpdated = repository.save(product);
		
		log.info("Product updated with ID: {}", id);
		
		return productUpdated;
	}

	@Override
	@Transactional(readOnly = true)
	public Product findById(Long id) {
		
		log.info("Searching product with ID: {}", id);
		

		return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", id);
                    return new RuntimeException("Producto no encontrado: " + id);
                });

	}

	@Override
	@Transactional(readOnly = true)
	public Page<Product> findAll(String name, Boolean active, int page, int size, String sort) {
		
		log.info("Product list with filters name='{}', active='{}'", name, active);
		
		return repository.findAllPaged(name, active, page, size, sort);
	}

	@Override
	public void delete(Long id) {
		
		log.info("Logical product deletion with ID: {}", id);
		
		Product product = findById(id);
		product.setActive(false);
		repository.save(product);
		
		log.info("Logical product deletion done with ID: {}", id);
	}

}
