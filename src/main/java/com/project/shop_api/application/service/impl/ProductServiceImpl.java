package com.project.shop_api.application.service.impl;

import org.springframework.data.domain.Page;

import com.project.shop_api.application.service.ProductService;
import com.project.shop_api.domain.model.Product;

public class ProductServiceImpl implements ProductService{

	@Override
	public Product create(Product product) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Product update(Long id, Product product) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Product findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Product> findAll(String name, Boolean active, int page, int size, String sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		
	}

}
