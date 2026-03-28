package com.project.shop_api.application.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.project.shop_api.application.common.exception.BadRequestException;
import com.project.shop_api.application.common.exception.ConflictException;
import com.project.shop_api.application.common.exception.ResourceNotFoundException;
import com.project.shop_api.application.common.exception.ValidationException;
import com.project.shop_api.application.service.impl.ProductServiceImpl;
import com.project.shop_api.domain.model.Product;
import com.project.shop_api.domain.repository.ProductRepository;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl service;

    private Product product;

    @BeforeEach
    void setup() {
        product = Product.builder()
                .id(1L)
                .sku("ABC-123")
                .name("Test Product")
                .price(BigDecimal.TEN)
                .stock(10)
                .active(true)
                .build();
    }

    // ================================================================
    // CREATE
    // ================================================================

    @Test
    void create_shouldThrowConflict_whenSkuExists() {
        when(repository.existsBySku("ABC-123")).thenReturn(true);

        Product newProduct = Product.builder()
                .sku("ABC-123")
                .price(BigDecimal.TEN)
                .build();

        assertThrows(ConflictException.class, () -> service.create(newProduct));

        verify(repository).existsBySku("ABC-123");
        verify(repository, never()).save(any());
    }

    @Test
    void create_shouldThrowValidation_whenPriceInvalid() {
        when(repository.existsBySku("NEW-001")).thenReturn(false);

        Product newProduct = Product.builder()
                .sku("NEW-001")
                .price(BigDecimal.ZERO) // invalid price
                .build();

        assertThrows(ValidationException.class, () -> service.create(newProduct));

        verify(repository).existsBySku("NEW-001");
        verify(repository, never()).save(any());
    }

    @Test
    void create_shouldSave_whenValid() {
        when(repository.existsBySku("NEW-001")).thenReturn(false);

        Product newProduct = Product.builder()
                .sku("NEW-001")
                .price(BigDecimal.TEN)
                .build();

        Product saved = Product.builder()
                .id(99L)
                .sku("NEW-001")
                .price(BigDecimal.TEN)
                .build();

        when(repository.save(newProduct)).thenReturn(saved);

        Product result = service.create(newProduct);

        assertEquals(99L, result.getId());
        verify(repository).save(newProduct);
    }

    // ================================================================
    // UPDATE
    // ================================================================

    @Test
    void update_shouldThrowNotFound_whenProductDoesNotExist() {
        when(repository.findById(5L)).thenReturn(Optional.empty());

        Product updated = Product.builder()
                .sku("AAA-111")
                .price(BigDecimal.TEN)
                .build();

        assertThrows(ResourceNotFoundException.class, () -> service.update(5L, updated));
    }

    @Test
    void update_shouldThrowConflict_whenChangingToExistingSku() {
        Product existing = Product.builder()
                .id(5L)
                .sku("AAA-111")
                .price(BigDecimal.TEN)
                .active(true)
                .build();

        Product update = Product.builder()
                .sku("BBB-999")
                .price(BigDecimal.TEN)
                .build();

        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        when(repository.existsBySku("BBB-999")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.update(5L, update));
    }

    @Test
    void update_shouldThrowValidation_whenPriceInvalid() {
        Product existing = Product.builder()
                .id(5L)
                .sku("AAA-111")
                .price(BigDecimal.TEN)
                .active(true)
                .build();

        Product update = Product.builder()
                .sku("AAA-111")
                .price(BigDecimal.ZERO) // invalid
                .build();

        when(repository.findById(5L)).thenReturn(Optional.of(existing));

        assertThrows(ValidationException.class, () -> service.update(5L, update));
    }

    @Test
    void update_shouldSave_whenValid() {
        Product existing = Product.builder()
                .id(5L)
                .sku("AAA-111")
                .price(BigDecimal.TEN)
                .active(true)
                .build();

        Product update = Product.builder()
                .sku("AAA-111")
                .price(BigDecimal.TEN)
                .build();

        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product result = service.update(5L, update);

        assertEquals(5L, result.getId());
        verify(repository).save(update);
    }

    // ================================================================
    // FIND BY ID
    // ================================================================

    @Test
    void findById_shouldThrowNotFound_whenMissing() {
        when(repository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(10L));
    }

    @Test
    void findById_shouldReturnProduct() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        Product result = service.findById(1L);

        assertEquals(1L, result.getId());
        verify(repository).findById(1L);
    }

    // ================================================================
    // FIND ALL
    // ================================================================

    @Test
    void findAll_shouldThrowBadRequest_whenPageInvalid() {
        assertThrows(BadRequestException.class,
                () -> service.findAll(null, null, -1, 10, "name,asc"));
    }

    @Test
    void findAll_shouldThrowBadRequest_whenSizeInvalid() {
        assertThrows(BadRequestException.class,
                () -> service.findAll(null, null, 0, 0, "name,asc"));
    }

    @Test
    void findAll_shouldReturnPage() {
        Page<Product> page = new PageImpl<>(Collections.singletonList(product));

        when(repository.findAllPaged(null, null, 0, 10, "name,asc"))
                .thenReturn(page);

        Page<Product> result =
                service.findAll(null, null, 0, 10, "name,asc");

        assertEquals(1, result.getTotalElements());
    }

    // ================================================================
    // DELETE (BAJA LÓGICA)
    // ================================================================

    @Test
    void delete_shouldThrowBadRequest_whenAlreadyInactive() {
        Product inactive = Product.builder()
                .id(1L)
                .active(false)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(inactive));

        assertThrows(BadRequestException.class, () -> service.delete(1L));
    }

    @Test
    void delete_shouldMarkInactive_whenValid() {
        Product active = Product.builder()
                .id(1L)
                .active(true)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(active));

        service.delete(1L);

        assertFalse(active.isActive());
        verify(repository).save(active);
    }
}

