package com.nguyenthimynguyen.example10.security.services;

import com.nguyenthimynguyen.example10.cafe.entity.Product;
import com.nguyenthimynguyen.example10.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    // ✅ Lấy toàn bộ sản phẩm
    public List<Product> getAll() {
        return repo.findAll();
    }

    // ✅ Tìm kiếm theo tên
    public List<Product> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return repo.findAll();
        }
        return repo.findByNameContainingIgnoreCase(keyword);
    }

    // ✅ Lọc theo danh mục + tìm kiếm
    public List<Product> getFiltered(String category, String keyword) {
        List<Product> products = repo.findAll();

        return products.stream()
                .filter(p -> category == null
                        || (p.getCategory() != null
                            && p.getCategory().getName() != null
                            && p.getCategory().getName().equalsIgnoreCase(category)))
                .filter(p -> keyword == null
                        || p.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    // ✅ Lấy sản phẩm theo ID
    public Optional<Product> getById(Long id) {
        return repo.findById(id);
    }

    // ✅ Thêm mới
    public Product create(Product product) {
        return repo.save(product);
    }

    // ✅ Cập nhật
public Product update(Long id, Product product) {
    return repo.findById(id).map(p -> {

        p.setName(product.getName());
        p.setDescription(product.getDescription());
        p.setPrice(product.getPrice());
        p.setCategory(product.getCategory());
        p.setPromotions(product.getPromotions());

        // 🔥 Giữ ảnh cũ nếu FE không gửi ảnh mới
        if (product.getImageUrl() != null && !product.getImageUrl().isBlank()) {
            p.setImageUrl(product.getImageUrl());
        }

        // 🔥 Bạn bị thiếu 2 dòng này
        p.setStockQuantity(product.getStockQuantity());
        p.setIsActive(product.getIsActive());

        return repo.save(p);
    }).orElseThrow(() -> new RuntimeException("Product not found"));
}

    // ✅ Xóa sản phẩm
    public void delete(Long id) {
        repo.deleteById(id);
    }
// ✅ Lấy sản phẩm theo ID (trả về Product, nếu không có thì báo lỗi)
public Product findById(Long id) {
    return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
}
}


