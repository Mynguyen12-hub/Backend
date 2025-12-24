package com.nguyenthimynguyen.example10.controllers;


import com.nguyenthimynguyen.example10.cafe.entity.Category;
import com.nguyenthimynguyen.example10.security.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    // Lấy tất cả
    @GetMapping
    public List<Category> getAll() {
        return service.getAll();
    }

    // Lấy 1 category
    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Thêm mới
    @PostMapping
    public Category create(@RequestBody Category category) {
        return service.create(category);
    }

    // Sửa
    @PutMapping("/{id}")
    public Category update(@PathVariable Long id, @RequestBody Category category) {
        return service.update(id, category);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}