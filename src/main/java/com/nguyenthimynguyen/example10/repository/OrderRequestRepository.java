package com.nguyenthimynguyen.example10.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.nguyenthimynguyen.example10.cafe.entity.OrderRequest;

import java.util.List;

public interface OrderRequestRepository extends JpaRepository<OrderRequest, Long> {
    List<OrderRequest> findByConfirmedFalse(); // các yêu cầu chưa được nhân viên xác nhận
    List<OrderRequest> findByTableId(Long tableId); // lấy yêu cầu theo bàn
}
