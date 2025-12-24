package com.nguyenthimynguyen.example10.dto;

import java.math.BigDecimal;
import java.util.List;

public class ApplyPromotionRequest {
    private Long promotionId;
    private List<Long> productIds; // optional, nếu promotion áp dụng cho sản phẩm cụ thể
    private BigDecimal billTotal; // thêm
    private List<ProductDto> billProducts; // thêm

    // getters và setters
    public Long getPromotionId() { return promotionId; }
    public void setPromotionId(Long promotionId) { this.promotionId = promotionId; }

    public List<Long> getProductIds() { return productIds; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }

    public BigDecimal getBillTotal() { return billTotal; }
    public void setBillTotal(BigDecimal billTotal) { this.billTotal = billTotal; }

    public List<ProductDto> getBillProducts() { return billProducts; }
    public void setBillProducts(List<ProductDto> billProducts) { this.billProducts = billProducts; }
}
