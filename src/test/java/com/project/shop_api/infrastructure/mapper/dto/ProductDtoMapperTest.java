package com.project.shop_api.infrastructure.mapper.dto;


import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.project.shop_api.domain.model.Product;
import com.project.shop_api.infrastructure.rest.dto.request.ProductRequest;
import com.project.shop_api.infrastructure.rest.dto.response.ProductResponse;

class ProductDtoMapperTest {

    private final ProductDtoMapper mapper = Mappers.getMapper(ProductDtoMapper.class);

    // ============================================================
    // REQUEST → DOMAIN
    // ============================================================
    @Test
    void toDomain_shouldMapRequestCorrectly() {

        ProductRequest req = new ProductRequest();
        req.setSku("ABC-111");
        req.setName("Laptop");
        req.setDescription("Gaming");
        req.setPrice(BigDecimal.TEN);
        req.setStock(5);
        req.setActive(true);

        Product product = mapper.toDomain(req);

        assertEquals("ABC-111", product.getSku());
        assertEquals("Laptop", product.getName());
        assertEquals("Gaming", product.getDescription());
        assertEquals(BigDecimal.TEN, product.getPrice());
        assertEquals(5, product.getStock());
        assertTrue(product.isActive());
    }

    // ============================================================
    // DOMAIN → RESPONSE
    // ============================================================
    @Test
    void toResponse_shouldMapDomainCorrectly() {

        Product product = Product.builder()
                .id(10L)
                .sku("XYZ-999")
                .name("Keyboard")
                .description("Mechanical")
                .price(BigDecimal.valueOf(50))
                .stock(12)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ProductResponse res = mapper.toResponse(product);

        assertEquals(10L, res.getId());
        assertEquals("XYZ-999", res.getSku());
        assertEquals("Keyboard", res.getName());
        assertEquals("Mechanical", res.getDescription());
        assertEquals(BigDecimal.valueOf(50), res.getPrice());
        assertEquals(12, res.getStock());
        assertTrue(res.isActive());
    }
}

