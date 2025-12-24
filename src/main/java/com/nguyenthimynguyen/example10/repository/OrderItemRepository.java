package com.nguyenthimynguyen.example10.repository;


import com.nguyenthimynguyen.example10.cafe.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
  List<OrderItem> findByOrderId(Long orderId);
}
