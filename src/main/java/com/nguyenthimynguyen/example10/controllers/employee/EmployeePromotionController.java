package com.nguyenthimynguyen.example10.controllers.employee;

import com.nguyenthimynguyen.example10.cafe.entity.Promotion;
import com.nguyenthimynguyen.example10.cafe.entity.enums.PromotionType;
import com.nguyenthimynguyen.example10.cafe.entity.Product;
import com.nguyenthimynguyen.example10.dto.ApplyPromotionRequest;
import com.nguyenthimynguyen.example10.dto.ApplyPromotionResponse;
import com.nguyenthimynguyen.example10.security.services.PromotionService;
import com.nguyenthimynguyen.example10.dto.ProductDto;

import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employee/promotions")
public class EmployeePromotionController {

    private final PromotionService promotionService;

    public EmployeePromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    /**
     * Áp mã giảm giá vào bill
     * @param request: promotionId + optional danh sách productIds
     * @param billTotal: tổng bill
     * @param billProducts: danh sách sản phẩm trong bill (có getPrice())
     */
    @GetMapping
public List<Promotion> getAllPromotions() {
    return promotionService.getAll(); // Trả về tất cả khuyến mãi đang active
}

@PostMapping("/apply")
public ApplyPromotionResponse applyPromotion(@RequestBody ApplyPromotionRequest request) {

    BigDecimal billTotal = request.getBillTotal();
    List<ProductDto> billProducts = request.getBillProducts();

    Promotion promotion = promotionService.validatePromotion(request.getPromotionId());

    BigDecimal discount = BigDecimal.ZERO;

    if (promotion.getType() == PromotionType.PRODUCT) {
        Set<Long> promoProductIds = promotion.getProducts() == null ? Set.of()
                : promotion.getProducts().stream().map(Product::getId).collect(Collectors.toSet());

        for (ProductDto p : billProducts) {
            if (promoProductIds.isEmpty() || promoProductIds.contains(p.getId())) {
                BigDecimal price = p.getPrice();
                if (promotion.getDiscountPercentage() != null) {
                    discount = discount.add(price.multiply(promotion.getDiscountPercentage())
                            .divide(BigDecimal.valueOf(100)));
                } else if (promotion.getDiscountAmount() != null) {
                    discount = discount.add(promotion.getDiscountAmount());
                }
            }
        }
    } else if (promotion.getType() == PromotionType.TOTAL) {
        if (promotion.getDiscountPercentage() != null) {
            discount = billTotal.multiply(promotion.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100));
        } else if (promotion.getDiscountAmount() != null) {
            discount = promotion.getDiscountAmount();
        }
    }

    BigDecimal finalTotal = billTotal.subtract(discount).max(BigDecimal.ZERO);

    ApplyPromotionResponse response = new ApplyPromotionResponse();
    response.setOriginalTotal(billTotal);
    response.setDiscountAmount(discount);
    response.setFinalTotal(finalTotal);

    return response;
}
}
