package com.nguyenthimynguyen.example10.controllers.employee;

import com.nguyenthimynguyen.example10.cafe.entity.Payment;
import com.nguyenthimynguyen.example10.cafe.entity.enums.PaymentStatus;
import com.nguyenthimynguyen.example10.security.services.OrderService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/employee")
public class EmployeePaymentController {

    private final OrderService orderService;

    public EmployeePaymentController(OrderService orderService) {
        this.orderService = orderService;
    }


@PutMapping("/payments/order/{id}/status")
public ResponseEntity<Payment> updatePaymentStatus(
        @PathVariable Long id,
        @RequestParam PaymentStatus status) {
    Payment payment = orderService.updatePaymentStatus(id, status);
    return ResponseEntity.ok(payment);
}
}
