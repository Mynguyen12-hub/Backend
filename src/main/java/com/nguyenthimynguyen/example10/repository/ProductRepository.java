package com.nguyenthimynguyen.example10.repository;

import com.nguyenthimynguyen.example10.cafe.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProductRepository extends JpaRepository<Product, Long> {
List<Product> findByNameContainingIgnoreCase(String keyword);
}
