package com.nguyenthimynguyen.example10.dto;

import lombok.Data;

@Data
public class AddProductRequest {
    private Long tableId;
    private Long productId;
    private int quantity;
    private String promotion;    // promotion code, có thể null
    private Long employeeId;     // id nhân viên, có thể null

    // ===== Getters & Setters =====
    public Long getTableId() { return tableId; }
    public void setTableId(Long tableId) { this.tableId = tableId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getPromotion() { return promotion; }
    public void setPromotion(String promotion) { this.promotion = promotion; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
}