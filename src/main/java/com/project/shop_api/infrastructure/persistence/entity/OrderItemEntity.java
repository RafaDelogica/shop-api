package com.project.shop_api.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items", indexes = {
        @Index(name = "idx_orderitem_order", columnList = "order_id"),
        @Index(name = "idx_orderitem_product", columnList = "product_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//@EqualsAndHashCode(of = "id")
public class OrderItemEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private OrderEntity order;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ProductEntity product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;
}
