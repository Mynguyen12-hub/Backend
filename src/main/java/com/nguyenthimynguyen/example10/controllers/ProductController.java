package com.nguyenthimynguyen.example10.controllers;

import com.nguyenthimynguyen.example10.cafe.entity.Product;
import com.nguyenthimynguyen.example10.security.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "http://localhost:3000") // Cho phép React truy cập
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // ✅ Lấy danh sách sản phẩm, có thể lọc theo danh mục & tìm kiếm theo tên
    @GetMapping
    public List<Product> getFilteredProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword
    ) {
        return service.getFiltered(category, keyword);
    }

    // ✅ Lấy 1 sản phẩm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Thêm mới sản phẩm
    @PostMapping
    public Product create(@RequestBody Product product) {
        return service.create(product);
    }

    // ✅ Cập nhật sản phẩm
    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        return service.update(id, product);
    }

    // ✅ Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}
