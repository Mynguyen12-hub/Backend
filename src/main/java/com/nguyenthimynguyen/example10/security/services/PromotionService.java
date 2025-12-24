package com.nguyenthimynguyen.example10.security.services;

import com.nguyenthimynguyen.example10.cafe.entity.Promotion;
import com.nguyenthimynguyen.example10.repository.PromotionRepository;
import org.springframework.stereotype.Service;
import com.nguyenthimynguyen.example10.exception.NotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

public List<Promotion> getAll() {
    // Lấy tất cả khuyến mãi còn hiệu lực
    return promotionRepository.findAll(); // hoặc filter active
}

    public Promotion getById(Long id) {
        return promotionRepository.findById(id).orElse(null);
    }

    public Promotion create(Promotion promotion) {
        promotion.setCreatedAt(LocalDateTime.now());
        promotion.setUpdatedAt(LocalDateTime.now());
        return promotionRepository.save(promotion);
    }

    public Promotion update(Long id, Promotion promotion) {
        return promotionRepository.findById(id).map(existing -> {
            existing.setName(promotion.getName());
            existing.setDiscountPercentage(promotion.getDiscountPercentage());
            existing.setDiscountAmount(promotion.getDiscountAmount());
            existing.setStartDate(promotion.getStartDate());
            existing.setEndDate(promotion.getEndDate());
            existing.setIsActive(promotion.getIsActive());
            existing.setProducts(promotion.getProducts());
            existing.setUpdatedAt(LocalDateTime.now());
            return promotionRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Promotion not found"));
    }

    public void delete(Long id) {
        promotionRepository.deleteById(id);
    }

    // ==========================
    // Method validatePromotion
    // ==========================
    public Promotion validatePromotion(Long promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));

        LocalDate today = LocalDate.now();
        if (!Boolean.TRUE.equals(promotion.getIsActive())
            || (promotion.getStartDate() != null && promotion.getStartDate().isAfter(today))
            || (promotion.getEndDate() != null && promotion.getEndDate().isBefore(today))) {
            throw new RuntimeException("Promotion is not active");
        }

        return promotion;
    }
    public Promotion validatePromotionByName(String promotionName) {
        if (promotionName == null || promotionName.isBlank()) return null;
        Promotion promo = promotionRepository.findByName(promotionName)
                .orElseThrow(() -> new NotFoundException("Mã khuyến mãi không tồn tại: " + promotionName));
        if (promo.getIsActive() == null || !promo.getIsActive()) {
            throw new RuntimeException("Khuyến mãi không còn hiệu lực");
        }
        LocalDate today = LocalDate.now();
        if (promo.getStartDate() != null && today.isBefore(promo.getStartDate())) {
            throw new RuntimeException("Khuyến mãi chưa bắt đầu");
        }
        if (promo.getEndDate() != null && today.isAfter(promo.getEndDate())) {
            throw new RuntimeException("Khuyến mãi đã kết thúc");
        }
        return promo;
    }

    public Promotion validatePromotionById(Long id) {
        if (id == null) return null;
        Promotion promo = promotionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Promotion id not found: " + id));
        // same checks...
        return validatePromotionByName(promo.getName());
    }
public java.util.Optional<Promotion> getPromotionByCode(String code) {
    if (code == null || code.isBlank()) return java.util.Optional.empty();
    return promotionRepository.findByName(code);
}

}
