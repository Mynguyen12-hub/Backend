package com.nguyenthimynguyen.example10.dto;

import com.nguyenthimynguyen.example10.cafe.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long productId;
    private String productName; // Tên sản phẩm
    private Integer quantity;   // số lượng
    private BigDecimal price;   // giá sản phẩm

    // Constructor từ entity OrderItem
    public OrderItemDTO(OrderItem item) {
        this.productId = item.getProduct() != null ? item.getProduct().getId() : null;
        this.productName = item.getProduct() != null ? item.getProduct().getName() : null;
        this.quantity = item.getQuantity();
        this.price = item.getPrice();
    }
}
