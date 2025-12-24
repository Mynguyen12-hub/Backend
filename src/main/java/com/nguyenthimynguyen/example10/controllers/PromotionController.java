package com.nguyenthimynguyen.example10.controllers;


import com.nguyenthimynguyen.example10.cafe.entity.Promotion;
import org.springframework.web.bind.annotation.*;
import com.nguyenthimynguyen.example10.security.services.PromotionService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/promotions")
public class PromotionController {
    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping
    public List<Promotion> getAll() {
        return promotionService.getAll();
    }

    @GetMapping("/{id}")
    public Promotion getById(@PathVariable Long id) {
        return promotionService.getById(id);
    }

    @PostMapping
    public Promotion create(@RequestBody Promotion promotion) {
        return promotionService.create(promotion);
    }

    @PutMapping("/{id}")
    public Promotion update(@PathVariable Long id, @RequestBody Promotion promotion) {
        return promotionService.update(id, promotion);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        promotionService.delete(id);
    }
}
