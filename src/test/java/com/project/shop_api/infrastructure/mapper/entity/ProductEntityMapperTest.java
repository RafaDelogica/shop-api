package com.project.shop_api.infrastructure.mapper.entity;


import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.project.shop_api.domain.model.Product;
import com.project.shop_api.infrastructure.persistence.entity.ProductEntity;
import com.project.shop_api.infrastructure.rest.dto.response.ProductResponse;

class ProductEntityMapperTest {

    private final ProductEntityMapper mapper = Mappers.getMapper(ProductEntityMapper.class);

    // ============================================================
    // DOMAIN → ENTITY
    // ============================================================
    @Test
    void toEntity_shouldMapDomainCorrectly() {

        Product product = Product.builder()
                .id(1L)
                .sku("ABC-111")
                .name("Monitor")
                .description("4K")
                .price(BigDecimal.valueOf(200))
                .stock(8)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ProductEntity entity = mapper.toEntity(product);

        assertEquals(1L, entity.getId());
        assertEquals("ABC-111", entity.getSku());
        assertEquals("Monitor", entity.getName());
        assertEquals("4K", entity.getDescription());
        assertEquals(BigDecimal.valueOf(200), entity.getPrice());
        assertEquals(8, entity.getStock());
        assertTrue(entity.isActive());
    }

    // ============================================================
    // ENTITY → DOMAIN
    // ============================================================
    @Test
    void toDomain_shouldMapEntityCorrectly() {

        ProductEntity entity = ProductEntity.builder()
                .id(77L)
                .sku("ZZZ-777")
                .name("Mouse")
                .description("Wireless")
                .price(BigDecimal.valueOf(25))
                .stock(20)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product product = mapper.toDomain(entity);

        assertEquals(77L, product.getId());
        assertEquals("ZZZ-777", product.getSku());
        assertEquals("Mouse", product.getName());
        assertEquals("Wireless", product.getDescription());
        assertEquals(BigDecimal.valueOf(25), product.getPrice());
        assertEquals(20, product.getStock());
        assertTrue(product.isActive());
    }
    
    @Test
    void toDomain_shouldReturnNull_whenRequestIsNull() {
        Product product = mapper.toDomain(null);
        assertNull(product);
    }
    
    @Test
    void toResponse_shouldReturnNull_whenDomainIsNull() {
    	ProductEntity response = mapper.toEntity(null);
        assertNull(response);
    }
}

