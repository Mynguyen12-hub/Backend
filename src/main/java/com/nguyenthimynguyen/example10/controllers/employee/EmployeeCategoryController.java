package com.nguyenthimynguyen.example10.controllers.employee;

import com.nguyenthimynguyen.example10.cafe.entity.Category;
import com.nguyenthimynguyen.example10.security.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/categories")
public class EmployeeCategoryController {

    private final CategoryService service;

    public EmployeeCategoryController(CategoryService service) {
        this.service = service;
    }

    // Lấy tất cả categories (GET)
    @GetMapping
    public List<Category> getAll() {
        return service.getAll();
    }

    // Lấy chi tiết 1 category (GET)
    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Thêm category mới (POST)
    @PostMapping
    public Category create(@RequestBody Category category) {
        return service.create(category);
    }

    // Cập nhật category (PUT)
    @PutMapping("/{id}")
    public Category update(@PathVariable Long id, @RequestBody Category category) {
        return service.update(id, category);
    }

    // Xóa category (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
