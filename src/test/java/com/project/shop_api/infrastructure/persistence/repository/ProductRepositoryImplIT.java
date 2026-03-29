//package com.project.shop_api.infrastructure.persistence.repository;
//
//import com.project.shop_api.config.TestcontainersConfig;
//import com.project.shop_api.domain.model.Product;
//import com.project.shop_api.infrastructure.mapper.entity.ProductEntityMapper;
//import com.project.shop_api.infrastructure.persistence.entity.ProductEntity;
//import com.project.shop_api.infrastructure.persistence.repository.impl.ProductRepositoryImpl;
//import com.project.shop_api.infrastructure.persistence.specification.ProductSpecification;
//
//import org.junit.jupiter.api.Test;
//
//import org.mapstruct.factory.Mappers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.domain.Page;
//import org.springframework.test.context.ContextConfiguration;
//
//import java.math.BigDecimal;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//@ContextConfiguration(initializers = TestcontainersConfig.Initializer.class)
//@Import({
//        ProductRepositoryImpl.class,
//        ProductEntityMapper.class,
//        ProductSpecification.class,
//        JpaProductRepository.class
//})
//class ProductRepositoryImplIT {
//
//    @Autowired
//    private ProductRepositoryImpl repository;
//
//    @Autowired
//    private JpaProductRepository jpa;
//
//    private ProductEntityMapper mapper = Mappers.getMapper(ProductEntityMapper.class);
//
//    private Product newDomain() {
//        return Product.builder()
//                .sku("SKU-100")
//                .name("Laptop")
//                .description("Gaming")
//                .price(BigDecimal.valueOf(1000))
//                .stock(5)
//                .active(true)
//                .build();
//    }
//
//    @Test
//    void save_shouldPersistDomainCorrectly() {
//        Product product = newDomain();
//
//        Product saved = repository.save(product);
//
//        assertNotNull(saved.getId());
//        assertEquals("SKU-100", saved.getSku());
//
//        ProductEntity entity = jpa.findById(saved.getId()).orElseThrow();
//        assertEquals("Laptop", entity.getName());
//    }
//
//    @Test
//    void findById_shouldReturnDomain() {
//        Product saved = repository.save(newDomain());
//
//        Product found = repository.findById(saved.getId()).orElse(null);
//
//        assertNotNull(found);
//        assertEquals(saved.getId(), found.getId());
//    }
//
//    @Test
//    void existsBySku_shouldReturnTrue() {
//        repository.save(newDomain());
//        assertTrue(repository.existsBySku("SKU-100"));
//    }
//
//    @Test
//    void findAllPaged_shouldReturnPage() {
//        for (int i = 1; i <= 10; i++) {
//            repository.save(
//                    Product.builder()
//                            .sku("SKU-" + i)
//                            .name("Product " + i)
//                            .price(BigDecimal.TEN)
//                            .stock(5)
//                            .active(true)
//                            .build()
//            );
//        }
//
//        Page<Product> page = repository.findAllPaged(null, null, 0, 5, "name,asc");
//
//        assertEquals(5, page.getContent().size());
//        assertEquals(10, page.getTotalElements());
//    }
//
//    @Test
//    void findAllPaged_shouldFilterByName() {
//        repository.save(newDomain()); // Laptop
//        repository.save(Product.builder()
//                .sku("SKU-200")
//                .name("Lampara")
//                .price(BigDecimal.ONE)
//                .stock(2)
//                .active(true)
//                .build());
//
//        Page<Product> page = repository.findAllPaged("lap", null, 0, 10, "name,asc");
//
//        assertEquals(2, page.getTotalElements());
//    }
//
//    @Test
//    void findAllPaged_shouldFilterByActive() {
//        repository.save(newDomain()); // active=true
//        repository.save(Product.builder()
//                .sku("SKU-300")
//                .name("Mesa")
//                .price(BigDecimal.ONE)
//                .stock(2)
//                .active(false)
//                .build());
//
//        Page<Product> page = repository.findAllPaged(null, true, 0, 10, "name,asc");
//
//        assertEquals(1, page.getTotalElements());
//        assertTrue(page.getContent().get(0).isActive());
//    }
//}