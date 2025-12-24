package com.nguyenthimynguyen.example10.dto;

import com.nguyenthimynguyen.example10.cafe.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;
    private String tableNumber;
    private String employeeName;
    private LocalDateTime createdAt;
    private BigDecimal totalAmount;
    private String status;
    private List<OrderItemDTO> items;
    private String promotionName;       // thêm
    private BigDecimal promotionAmount; // thêm

    public OrderDTO(Order order) {
        this.orderId = order.getId();
        this.tableNumber = order.getTable() != null ? String.valueOf(order.getTable().getNumber()) : null;
        this.employeeName = order.getEmployee() != null ? order.getEmployee().getFullName() : null;
        this.createdAt = order.getCreatedAt();
        this.totalAmount = order.getTotalAmount();
        this.status = order.getStatus().name();
        this.items = order.getItems().stream()
                          .map(OrderItemDTO::new)
                          .collect(Collectors.toList());
        if (order.getPromotion() != null) {
            this.promotionName = order.getPromotion().getName();
            this.promotionAmount = order.getPromotion().getDiscountAmount();
        }
    }
}
