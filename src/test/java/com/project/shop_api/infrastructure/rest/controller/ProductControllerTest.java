package com.project.shop_api.infrastructure.rest.controller;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shop_api.application.common.exception.ConflictException;
import com.project.shop_api.application.common.exception.ResourceNotFoundException;
import com.project.shop_api.application.service.ProductService;
import com.project.shop_api.domain.model.Product;
import com.project.shop_api.infrastructure.mapper.dto.ProductDtoMapper;
import com.project.shop_api.infrastructure.rest.dto.request.ProductRequest;
import com.project.shop_api.infrastructure.rest.exception.GlobalExceptionHandler;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;


@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService service;

    @MockBean
    private ProductDtoMapper mapper;

    // ===============================================================
    // POST /api/products (happy path)
    // ===============================================================

    @Test
    void create_shouldReturnOk_whenValidRequest() throws Exception {

        ProductRequest req = new ProductRequest();
        req.setSku("ABC-111");
        req.setName("Laptop");
        req.setPrice(BigDecimal.TEN);
        req.setStock(5);
        req.setActive(true);

        Product domain = Product.builder()
                .id(1L).sku("ABC-111").name("Laptop")
                .price(BigDecimal.TEN).stock(5).active(true)
                .build();

        when(mapper.toDomain(any())).thenReturn(domain);
        when(service.create(domain)).thenReturn(domain);
        when(mapper.toResponse(domain)).thenReturn(
                new com.project.shop_api.infrastructure.rest.dto.response.ProductResponse()
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }


    // ===============================================================
    // POST /api/products (SKU duplicated → CONFLICT 409)
    // ===============================================================

    @Test
    void create_shouldReturnConflict_whenSkuExists() throws Exception {

        ProductRequest req = new ProductRequest();
        req.setSku("DUPL-001");
        req.setName("Laptop");
        req.setPrice(BigDecimal.TEN);

        Product domain = Product.builder()
                .sku("DUPL-001")
                .price(BigDecimal.TEN)
                .build();

        when(mapper.toDomain(any())).thenReturn(domain);
        when(service.create(domain)).thenThrow(new ConflictException("SKU duplicado"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("SKU duplicado"));
    }


    // ===============================================================
    // GET /api/products/{id} (happy path)
    // ===============================================================

    @Test
    void findById_shouldReturnProduct_whenExists() throws Exception {

        Product domain = Product.builder()
                .id(1L).sku("ABC").name("Laptop")
                .price(BigDecimal.TEN).active(true)
                .build();

        when(service.findById(1L)).thenReturn(domain);
        when(mapper.toResponse(domain)).thenReturn(
                new com.project.shop_api.infrastructure.rest.dto.response.ProductResponse()
        );

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk());
    }


    // ===============================================================
    // GET /api/products/{id} → NOT FOUND (404)
    // ===============================================================

    @Test
    void findById_shouldReturnNotFound_whenMissing() throws Exception {

        when(service.findById(99L))
                .thenThrow(new ResourceNotFoundException("No existe"));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("No existe"));
    }


    // ===============================================================
    // DELETE LOGICAL DELETE 204 NO CONTENT
    // ===============================================================

    @Test
    void delete_shouldReturnNoContent_whenValid() throws Exception {

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }


    // ===============================================================
    // GET /api/products with pagination
    // ===============================================================

    @Test
    void findAll_shouldReturnPage() throws Exception {

        Product p = Product.builder()
                .id(1L).sku("ABC").name("Laptop")
                .price(BigDecimal.TEN).active(true)
                .build();

        Page<Product> page = new PageImpl<>(List.of(p));

        when(service.findAll(null, null, 0, 20, "name,asc"))
                .thenReturn(page);

        when(mapper.toResponse(p))
                .thenReturn(new com.project.shop_api.infrastructure.rest.dto.response.ProductResponse());

        mockMvc.perform(get("/api/products?page=0&size=20&sort=name,asc"))
                .andExpect(status().isOk());
    }
    
    // ===============================================================
    // UPDATE /api/products OK
    // ===============================================================
    
    @Test
    void update_shouldReturnOk_whenValidRequest() throws Exception {

        ProductRequest req = new ProductRequest();
        req.setSku("ABC-100");
        req.setName("Updated Name");
        req.setPrice(BigDecimal.TEN);
        req.setStock(5);
        req.setActive(true);

        Product domain = Product.builder()
                .id(1L)
                .sku("ABC-100")
                .name("Updated Name")
                .price(BigDecimal.TEN)
                .stock(5)
                .active(true)
                .build();

        when(mapper.toDomain(any())).thenReturn(domain);
        when(service.update(eq(1L), eq(domain))).thenReturn(domain);
        when(mapper.toResponse(domain)).thenReturn(
                new com.project.shop_api.infrastructure.rest.dto.response.ProductResponse()
        );

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
    
    // ===============================================================
    // UPDATE /api/products RESOURCE NOT FOUND
    // ===============================================================
    
    @Test
    void update_shouldReturnNotFound_whenProductMissing() throws Exception {

        ProductRequest req = new ProductRequest();
        req.setSku("ABC-100");
        req.setName("Updated");
        req.setPrice(BigDecimal.TEN);

        when(mapper.toDomain(any())).thenReturn(Product.builder().build());
        when(service.update(eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("Producto no encontrado"));

        mockMvc.perform(put("/api/products/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Producto no encontrado"));
    }
    
    // ===============================================================
    // UPDATE /api/products CONFLICT
    // ===============================================================
    @Test
    void update_shouldReturnConflict_whenSkuExists() throws Exception {

        ProductRequest req = new ProductRequest();
        req.setSku("DUPL-001");
        req.setName("Laptop");
        req.setPrice(BigDecimal.TEN);

        when(mapper.toDomain(any())).thenReturn(Product.builder().build());
        when(service.update(eq(1L), any()))
                .thenThrow(new ConflictException("SKU duplicado"));

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("SKU duplicado"));
    }
    
    // ===============================================================
    // UPDATE /api/products VALIDATION ERROR
    // ===============================================================
//    @Test
//    void update_shouldReturnValidationError_whenDtoInvalid() throws Exception {
//
//        ProductRequest req = new ProductRequest();
//        req.setSku("ABC-100");
//        req.setName("");                     // @NotBlank -> error
//        req.setPrice(BigDecimal.valueOf(-1)); // @DecimalMin -> error
//
//        mockMvc.perform(put("/api/products/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
//                .andExpect(jsonPath("$.details").isArray())
//                .andExpect(jsonPath("$.details.length()").value(2)); // name + price
//    }

}

