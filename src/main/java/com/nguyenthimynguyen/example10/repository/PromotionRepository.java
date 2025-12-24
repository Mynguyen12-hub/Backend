package com.nguyenthimynguyen.example10.repository;

import com.nguyenthimynguyen.example10.cafe.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PromotionRepository extends JpaRepository<Promotion, Long>{
      Optional<Promotion> findByName(String name);
}