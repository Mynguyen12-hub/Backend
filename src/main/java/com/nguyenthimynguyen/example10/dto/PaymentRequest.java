package com.nguyenthimynguyen.example10.dto;

import com.nguyenthimynguyen.example10.cafe.entity.enums.PaymentMethod;

public class PaymentRequest {
    private Long orderId;
    private PaymentMethod method;

    private Long promotionId;
    private String promotionCode;

    // --- DÙNG KHI METHOD = BANK ---
    private String bankBin;
    private String accountNumber;
    private String ownerName;

    // GETTER - SETTER
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    public Long getPromotionId() { return promotionId; }
    public void setPromotionId(Long promotionId) { this.promotionId = promotionId; }

    public String getPromotionCode() { return promotionCode; }
    public void setPromotionCode(String promotionCode) { this.promotionCode = promotionCode; }

    public String getBankBin() { return bankBin; }
    public void setBankBin(String bankBin) { this.bankBin = bankBin; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}
