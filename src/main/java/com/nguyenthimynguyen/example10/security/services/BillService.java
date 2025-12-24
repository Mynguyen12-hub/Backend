package com.nguyenthimynguyen.example10.security.services;

import com.nguyenthimynguyen.example10.cafe.entity.Bill;
import com.nguyenthimynguyen.example10.cafe.entity.Order;
import com.nguyenthimynguyen.example10.cafe.entity.enums.PaymentMethod;
import com.nguyenthimynguyen.example10.cafe.entity.enums.PaymentStatus;
import com.nguyenthimynguyen.example10.repository.BillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BillService {

    private final BillRepository billRepo;

    public BillService(BillRepository billRepo) {
        this.billRepo = billRepo;
    }

    // Lấy tất cả hóa đơn
    public List<Bill> getAll() {
        return billRepo.findAll();
    }

    public Optional<Bill> getByOrderId(Long orderId) {
        return billRepo.findByOrderId(orderId);
    }

    // Tạo Bill từ Order (chưa có Payment)
    @Transactional
    public Bill createFromOrder(Order order) {
        if (order == null) throw new IllegalArgumentException("Order cannot be null");

        Bill bill = new Bill();
        bill.setOrder(order);
        bill.setTotalAmount(order.getTotalAmount());
        bill.setPaymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod() : PaymentMethod.CASH);
        bill.setPaymentStatus(PaymentStatus.PENDING); // ban đầu pending
        bill.setIssuedAt(LocalDateTime.now());
        bill.setNotes(order.getNotes());
        bill.setCreatedAt(LocalDateTime.now());
        bill.setUpdatedAt(LocalDateTime.now());

        return billRepo.save(bill);
    }
}
