package com.nguyenthimynguyen.example10.controllers;

import com.nguyenthimynguyen.example10.cafe.entity.*;
import com.nguyenthimynguyen.example10.cafe.entity.enums.OrderStatus;
import com.nguyenthimynguyen.example10.cafe.entity.enums.PaymentMethod;
import com.nguyenthimynguyen.example10.cafe.entity.enums.PaymentStatus;
import com.nguyenthimynguyen.example10.dto.OrderDTO;
import com.nguyenthimynguyen.example10.dto.OrderStatsPerDayDTO;
import com.nguyenthimynguyen.example10.dto.RevenueCountDTO;
import com.nguyenthimynguyen.example10.security.services.OrderService;
import com.nguyenthimynguyen.example10.exception.NotFoundException;
import com.nguyenthimynguyen.example10.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;



@CrossOrigin(origins = "http://localhost:3002")
@RestController
@RequestMapping("/api/admin/orders")
public class OrderController {

    private final OrderService orderService;

@Autowired
    private OrderRepository orderRepository;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public static class CreateOrderRequest {
        public Long tableId;
        public List<OrderItemDTO> items;
    }

    public static class OrderItemDTO {
        public Long productId;
        public int quantity;
    }

    // @GetMapping
    // public ResponseEntity<List<Order>> getAllOrders() {
    //     return ResponseEntity.ok(orderService.getAll());
    // }
    @GetMapping
public ResponseEntity<List<Order>> getAllOrders() {
    return ResponseEntity.ok(orderRepository.findAllWithDetails());
}
@GetMapping("/all-dto")
public ResponseEntity<List<OrderDTO>> getAllOrdersDTO() {
    List<OrderDTO> dtos = orderService.getAllOrdersDTO();
    return ResponseEntity.ok(dtos);
}
// @GetMapping
// public ResponseEntity<List<OrderDTO>> getAllOrders() {
//     List<OrderDTO> dtos = orderService.getAll().stream()
//         .map(OrderDTO::new)
//         .collect(Collectors.toList());
//     return ResponseEntity.ok(dtos);
// }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return orderService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest req) {
        TableEntity table = orderService.getTableById(req.tableId)
                .orElseThrow(() -> new NotFoundException("Bàn không tồn tại: " + req.tableId));

        List<OrderItem> items = req.items.stream().map(i -> {
            Product product = orderService.getProductById(i.productId)
                    .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại: " + i.productId));
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(i.quantity);
            return item;
        }).collect(Collectors.toList());

        Order created = orderService.createOrderForTable(table, items);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) Long employeeId) {

        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Trạng thái không hợp lệ: " + status);
        }

        Order updated = orderService.updateStatus(id, orderStatus, employeeId);
        return ResponseEntity.ok(updated);
    }

@PostMapping("/{id}/payment")
public ResponseEntity<Payment> createPayment(
        @PathVariable Long id,
        @RequestParam PaymentMethod method,
        @RequestParam BigDecimal amount,
        @RequestParam(required = false) String bankBin,
        @RequestParam(required = false) String accountNumber,
        @RequestParam(required = false) String ownerName
) {
    Payment payment = orderService.createPayment(id, amount, method, bankBin, accountNumber, ownerName);
    return ResponseEntity.ok(payment);
}

    @PutMapping("/payment/{paymentId}/status")
    public ResponseEntity<Payment> updatePaymentStatus(@PathVariable Long paymentId,
                                                       @RequestParam PaymentStatus status) {
        Payment payment = orderService.updatePaymentStatus(paymentId, status);
        return ResponseEntity.ok(payment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        try {
            orderService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

@GetMapping("/{id}/invoice")
public ResponseEntity<byte[]> exportInvoice(@PathVariable Long id) {
    // Gọi phương thức của service
    byte[] pdf = orderService.exportInvoicePdf(id); // chỉ cần id, service tự lấy Payment, QR code

    return ResponseEntity.ok()
            .header("Content-Type", "application/pdf")
            .header("Content-Disposition", "attachment; filename=invoice_" + id + ".pdf")
            .body(pdf);

}

// GET /api/admin/orders/revenue/daily?startDate=2025-11-01T00:00:00
// Top N sản phẩm bán chạy
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @GetMapping("/report/top-products")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingProducts(@RequestParam(defaultValue = "5") int topN) {
        return ResponseEntity.ok(orderService.getTopSellingProducts(topN));
    }

    // Doanh thu theo category
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @GetMapping("/report/revenue-by-category")
    public ResponseEntity<List<Map<String, Object>>> getRevenueByCategory() {
        return ResponseEntity.ok(orderService.getRevenueByCategory());
    }

    // Doanh thu theo ngày
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @GetMapping("/report/revenue-by-day")
    public ResponseEntity<List<Map<String, Object>>> getRevenueByDay() {
        return ResponseEntity.ok(orderService.getRevenueByDay());
    }
    @PutMapping("/admin/orders/{id}/status")
public ResponseEntity<OrderDTO> updateOrderStatus(
        @PathVariable Long id,
        @RequestParam OrderStatus status,
        @RequestParam(required = false) Long employeeId
) {
    Order updatedOrder = orderService.updateStatus(id, status, employeeId);
    return ResponseEntity.ok(new OrderDTO(updatedOrder));
}

}
