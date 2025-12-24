package com.nguyenthimynguyen.example10.cafe.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_request_items")
public class OrderRequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết tới OrderRequest
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_request_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OrderRequest orderRequest;

    // Liên kết tới Product
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    // Tạo constructor tiện dụng
    public OrderRequestItem(OrderRequest orderRequest, Product product, Integer quantity, BigDecimal price) {
        this.orderRequest = orderRequest;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }
}
