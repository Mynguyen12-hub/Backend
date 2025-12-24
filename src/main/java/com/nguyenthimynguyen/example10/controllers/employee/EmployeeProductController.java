package com.nguyenthimynguyen.example10.controllers.employee;

import com.nguyenthimynguyen.example10.cafe.entity.Product;
import com.nguyenthimynguyen.example10.security.services.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/products")
public class EmployeeProductController {

    private final ProductService service;

    public EmployeeProductController(ProductService service) {
        this.service = service;
    }

    // Lấy toàn bộ sản phẩm
    @GetMapping
    public List<Product> getAll() {
        return service.getAll();
    }

    // Tìm kiếm theo từ khóa
    @GetMapping("/search")
    public List<Product> search(@RequestParam(required = false) String keyword) {
        return service.search(keyword);
    }
}
