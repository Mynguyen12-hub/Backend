package com.nguyenthimynguyen.example10.repository;

import com.nguyenthimynguyen.example10.cafe.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface BillRepository extends JpaRepository<Bill, Long> {
 Optional<Bill> findByOrderId(Long orderId);
}
