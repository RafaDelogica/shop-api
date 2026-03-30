package com.project.shop_api.application.service.impl;

import com.project.shop_api.application.common.exception.BadRequestException;
import com.project.shop_api.application.common.exception.ConflictException;
import com.project.shop_api.application.common.exception.ResourceNotFoundException;
import com.project.shop_api.application.service.OrderService;
import com.project.shop_api.domain.model.Address;
import com.project.shop_api.domain.model.Customer;
import com.project.shop_api.domain.model.Order;
import com.project.shop_api.domain.model.OrderItem;
import com.project.shop_api.domain.model.Product;
import com.project.shop_api.domain.enums.OrderStatus;
import com.project.shop_api.domain.repository.OrderRepository;
import com.project.shop_api.domain.repository.CustomerRepository;
import com.project.shop_api.domain.repository.AddressRepository;
import com.project.shop_api.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final CustomerRepository customerRepository;
	private final AddressRepository addressRepository;
	private final ProductRepository productRepository;

	@Override
	public Order create(Order order) {

		log.info("Creating order for customerId={}", order.getCustomerId());

		// 1.Validate Customer
		Customer customer = customerRepository.findById(order.getCustomerId()).orElseThrow(
				() -> new ResourceNotFoundException("Customer not found with id " + order.getCustomerId()));

		// 2.Validate Address
		Address address = addressRepository.findById(order.getShippingAddressId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Shipping address not found with id " + order.getShippingAddressId()));

		// 3️.Validate if the address belong to the Customer
		boolean belongs = addressRepository.existsByIdAndCustomerId(order.getShippingAddressId(),
				order.getCustomerId());
		if (!belongs) {
			throw new BadRequestException("Shipping address does not belong to this customer");
		}

		// 4️.Validate items
		if (order.getItems() == null || order.getItems().isEmpty()) {
			throw new BadRequestException("Order must contain at least one item");
		}

		BigDecimal total = BigDecimal.ZERO;

		// 5.Validate stock, product active, freeze unitPrice, calculate lineTotal
		for (OrderItem item : order.getItems()) {

			Product product = productRepository.findById(item.getProductId()).orElseThrow(
					() -> new ResourceNotFoundException("Product not found with id " + item.getProductId()));

			if (!product.isActive()) {
				throw new ConflictException("Product " + product.getName() + " is inactive");
			}

			if (product.getStock() < item.getQuantity()) {
				throw new ConflictException("Insufficient stock for product " + product.getName());
			}

			// Freeze Price
			item.setUnitPrice(product.getPrice());

			// Calculate lineTotal
			BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

			item.setLineTotal(lineTotal);

			// Set Product name to return in the response dto
			item.setProductName(product.getName());

			// Add to the total order amount
			total = total.add(lineTotal);

			// Reduce stock
			product.setStock(product.getStock() - item.getQuantity());
			productRepository.save(product);
		}

		// 6️. Set total and initial state
		order.setTotal(total);
		order.setStatus(OrderStatus.CREATED);
		order.setOrderDate(LocalDateTime.now());

		// 7️. Save
		Order saved = orderRepository.save(order);

		// Recalculate lineTotal because DB does not store that field
		for (OrderItem item : saved.getItems()) {
			BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
			item.setLineTotal(lineTotal);
		}

		log.info("Order created id={}, total={}", saved.getId(), saved.getTotal());
		return saved;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Order> findAll(Long customerId, String fromDate, String toDate, String status, int page, int size) {

		log.info("Listing orders with filters");

		return orderRepository.findAll(customerId, fromDate, toDate, status, page, size);
	}

	@Override
	@Transactional(readOnly = true)
	public Order findById(Long id) {

		log.info("Finding order id={}", id);

		return orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
	}

	@Override
	public Order updateStatus(Long id, OrderStatus newStatus) {

		log.info("Updating order id={} -> {}", id, newStatus);

		Order order = findById(id);

		OrderStatus current = order.getStatus();

		// Check valid transactions
		switch (newStatus) {

		case PAID:
			if (current != OrderStatus.CREATED) {
				throw new ConflictException("Order can only move to PAID from CREATED");
			}
			break;

		case SHIPPED:
			if (current != OrderStatus.PAID) {
				throw new ConflictException("Order can only move to SHIPPED from PAID");
			}
			break;

		case CANCELLED:
			if (current == OrderStatus.SHIPPED) {
				throw new ConflictException("Order cannot be cancelled after being shipped");
			}
			// rollback in the stock if the order status is cancelled if its previous status
			// was not SHIPPED
			restoreStock(order);
			break;

		default:
			throw new BadRequestException("Invalid status");
		}

		order.setStatus(newStatus);

		Order saved = orderRepository.save(order);

		// Recalculate lineTotal because DB does not store that field
		for (OrderItem item : saved.getItems()) {
			BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
			item.setLineTotal(lineTotal);
		}

		log.info("Order status updated for id={}", id);
		return saved;
	}

	private void restoreStock(Order order) {

		log.info("Restoring stock for order id={}", order.getId());

		for (OrderItem item : order.getItems()) {

			Product product = productRepository.findById(item.getProductId())
					.orElseThrow(() -> new ResourceNotFoundException("Product not found for restore stock"));

			product.setStock(product.getStock() + item.getQuantity());
			productRepository.save(product);
		}
	}
}