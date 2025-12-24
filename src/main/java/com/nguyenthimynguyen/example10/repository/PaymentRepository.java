package com.nguyenthimynguyen.example10.repository;

import com.nguyenthimynguyen.example10.cafe.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Nếu cần các truy vấn tuỳ chỉnh có thể thêm ở đây
    Optional<Payment> findByOrderId(Long orderId);}
