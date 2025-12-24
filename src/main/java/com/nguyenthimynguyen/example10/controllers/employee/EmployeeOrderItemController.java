package com.nguyenthimynguyen.example10.controllers.employee;

import com.nguyenthimynguyen.example10.cafe.entity.OrderItem;
import com.nguyenthimynguyen.example10.security.services.OrderItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/order_items")
public class EmployeeOrderItemController {

    private final OrderItemService service;

    public EmployeeOrderItemController(OrderItemService service) {
        this.service = service;
    }

    // Lấy tất cả order item
    @GetMapping
    public ResponseEntity<List<OrderItem>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // Lấy order item theo id
    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Thêm món vào order
    @PostMapping("/order/{orderId}")
    public ResponseEntity<OrderItem> addItemToOrder(@PathVariable Long orderId,
                                                    @RequestBody OrderItem item) {
        OrderItem created = service.addItemToOrder(orderId, item);
        return ResponseEntity.ok(created);
    }

    // Cập nhật order item
    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> update(@PathVariable Long id, @RequestBody OrderItem item) {
        return ResponseEntity.ok(service.update(id, item));
    }

    // Xóa order item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
