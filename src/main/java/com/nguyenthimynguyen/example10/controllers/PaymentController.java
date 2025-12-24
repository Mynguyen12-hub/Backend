package com.nguyenthimynguyen.example10.controllers;

import com.nguyenthimynguyen.example10.cafe.entity.Payment;
import com.nguyenthimynguyen.example10.cafe.entity.enums.PaymentMethod;
import com.nguyenthimynguyen.example10.cafe.entity.enums.PaymentStatus;
import com.nguyenthimynguyen.example10.security.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Xem tất cả payment
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    // Tạo payment mới liên kết với đơn hàng
    @PostMapping("/create")
    public ResponseEntity<Payment> createPayment(
            @RequestParam Long orderId,
            @RequestParam BigDecimal amount,
            @RequestParam PaymentMethod method,
            @RequestParam(required = false) String bankBin,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) String ownerName
    ) {
        Payment payment = paymentService.createPayment(orderId, amount, method, bankBin, accountNumber, ownerName);
        return ResponseEntity.ok(payment);
    }

    // Cập nhật trạng thái payment
    @PutMapping("/{paymentId}/status")
    public ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable Long paymentId,
            @RequestParam PaymentStatus status) {

        Payment payment = paymentService.updatePaymentStatus(paymentId, status);
        return ResponseEntity.ok(payment);
    }
}
