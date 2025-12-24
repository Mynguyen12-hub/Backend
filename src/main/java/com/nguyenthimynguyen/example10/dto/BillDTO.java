package com.nguyenthimynguyen.example10.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.nguyenthimynguyen.example10.cafe.entity.Bill;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillDTO {
    private Long billId;
    private Long orderId;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String notes;
    private LocalDateTime issuedAt;

    public BillDTO(Bill bill) {
        this.billId = bill.getId();
        this.orderId = bill.getOrder() != null ? bill.getOrder().getId() : null;
        this.totalAmount = bill.getTotalAmount();
        this.paymentMethod = bill.getPaymentMethod() != null ? bill.getPaymentMethod().name() : null;
        this.paymentStatus = bill.getPaymentStatus() != null ? bill.getPaymentStatus().name() : null;
        this.notes = bill.getNotes();
        this.issuedAt = bill.getIssuedAt();
    }
}
