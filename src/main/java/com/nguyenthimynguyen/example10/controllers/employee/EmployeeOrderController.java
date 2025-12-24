package com.nguyenthimynguyen.example10.controllers.employee;

import com.nguyenthimynguyen.example10.cafe.entity.*;
import com.nguyenthimynguyen.example10.cafe.entity.enums.OrderStatus;
import com.nguyenthimynguyen.example10.cafe.entity.enums.PaymentMethod;
import com.nguyenthimynguyen.example10.cafe.entity.enums.PaymentStatus;
import com.nguyenthimynguyen.example10.exception.NotFoundException;
import com.nguyenthimynguyen.example10.repository.EmployeeRepository;
import com.nguyenthimynguyen.example10.repository.OrderRepository;
import com.nguyenthimynguyen.example10.repository.OrderRequestRepository;
import com.nguyenthimynguyen.example10.security.SecurityUtils;
import com.nguyenthimynguyen.example10.security.services.BillService;
import com.nguyenthimynguyen.example10.security.services.OrderService;
import com.nguyenthimynguyen.example10.security.services.PaymentService;
import com.nguyenthimynguyen.example10.security.services.ProductService;
import com.nguyenthimynguyen.example10.security.services.TableService;
import com.nguyenthimynguyen.example10.security.services.PromotionService;
import com.nguyenthimynguyen.example10.dto.AddProductRequest;
import com.nguyenthimynguyen.example10.dto.OrderItemDTO;
import com.nguyenthimynguyen.example10.dto.PaymentRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping("/api/employee")
@CrossOrigin(origins = "*")
public class EmployeeOrderController {

    private final OrderService orderService;
    private final OrderRequestRepository orderRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final TableService tableService;
    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final PromotionService promotionService;
    private final SecurityUtils securityUtils;
    private final PaymentService paymentService;
    private final BillService billService;

    public EmployeeOrderController(OrderService orderService,
                                   OrderRequestRepository orderRequestRepository,
                                   EmployeeRepository employeeRepository,
                                   TableService tableService,
                                   BillService billService,
                                   ProductService productService,
                                   OrderRepository orderRepository,
                                   PromotionService promotionService,
                                   PaymentService paymentService,
                                   SecurityUtils securityUtils) {
        this.orderService = orderService;
        this.orderRequestRepository = orderRequestRepository;
        this.employeeRepository = employeeRepository;
        this.tableService = tableService;
        this.productService = productService;
        this.orderRepository = orderRepository;
        this.promotionService = promotionService;
        this.securityUtils = securityUtils;
        this.paymentService = paymentService;
        this.billService = billService;
    }
@GetMapping("/{id}/invoice")
public ResponseEntity<byte[]> exportInvoice(@PathVariable Long id) {
    byte[] pdf = orderService.exportInvoicePdf(id); 
    return ResponseEntity.ok()
            .header("Content-Type", "application/pdf")
            .header("Content-Disposition", "attachment; filename=invoice_" + id + ".pdf")
            .body(pdf);
}

    // =================== ORDER ===================
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

@PostMapping("/orders/create")
public ResponseEntity<Order> createOrder(@RequestParam Long tableId,
                                         @RequestBody List<OrderItemDTO> itemsDto,
                                         @RequestParam(required = false) Long employeeId) {

    TableEntity table = orderService.getTableById(tableId)
            .orElseThrow(() -> new NotFoundException("Bàn không tồn tại: " + tableId));

    Employee emp = null;
    if (employeeId != null) {
        emp = employeeRepository.findById(employeeId).orElse(null);
    }
    if (emp == null) {
        emp = securityUtils.getCurrentEmployee().orElse(null);
    }

    List<OrderItem> items = itemsDto.stream().map(dto -> {
        Product product = orderService.getProductById(dto.getProductId())
                .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại: " + dto.getProductId()));
        OrderItem oi = new OrderItem();
        oi.setProduct(product);
        oi.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 1);
        oi.setPrice(product.getPrice());
        oi.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())));
        return oi;
    }).toList();

    Order order = orderService.createOrderForTable(table, items);
    if (emp != null) {
        order.setEmployee(emp);
        orderRepository.save(order);
    }

    return ResponseEntity.ok(order);
}

    @PostMapping("/orders/add-product")
    public ResponseEntity<Order> addProductToTable(@RequestBody AddProductRequest req) {
        TableEntity table = tableService.getById(req.getTableId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bàn!"));

        Product product = productService.findById(req.getProductId());

        Long employeeId = req.getEmployeeId();

        Long promotionId = null;
        if (req.getPromotion() != null && !req.getPromotion().isEmpty()) {
            promotionId = promotionService.getPromotionByCode(req.getPromotion())
                                          .map(Promotion::getId)
                                          .orElse(null);
        }

        Order order = orderService.addProductToTable(
                table,
                product,
                req.getQuantity(),
                employeeId,
                promotionId
        );

        return ResponseEntity.ok(order);
    }

    @PostMapping("/orders/{orderId}/apply-promotion")
    public ResponseEntity<Order> applyPromotion(@PathVariable Long orderId,
                                                @RequestBody Map<String, Long> req) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order không tồn tại!"));

        Long promoId = req.get("promotionId");
        orderService.applyPromotionToOrder(order, promoId);

        return ResponseEntity.ok(order);
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Order> advanceOrderStatus(@PathVariable Long id,
                                                    @RequestParam OrderStatus nextStatus,
                                                    @RequestParam(required = false) Long employeeId) {
        Order order = orderService.advanceOrderStatus(id, nextStatus, employeeId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/orders/current")
    public ResponseEntity<List<Order>> getCurrentOrdersOfTable(@RequestParam Long tableId) {
        TableEntity table = orderService.getTableById(tableId)
                .orElseThrow(() -> new NotFoundException("Bàn không tồn tại: " + tableId));
        List<Order> orders = orderService.getCurrentOrdersOfTable(table);
        return ResponseEntity.ok(orders);
    }

    // =================== PAYMENT ===================
    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(orderService.getAllPayments());
    }

    @PutMapping("/payments/{id}/status")
    public ResponseEntity<Payment> updatePaymentStatus(@PathVariable Long id,
                                                       @RequestParam PaymentStatus status) {
        Payment payment = orderService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/payments/status/{orderId}")
    public ResponseEntity<PaymentStatus> getPaymentStatus(@PathVariable Long orderId) {
        Payment payment = orderService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(payment.getStatus());
    }

    // =================== CHECKOUT ===================
@PostMapping("/orders/{orderId}/checkout")
public ResponseEntity<?> checkoutOrder(
        @PathVariable Long orderId,
        @RequestBody PaymentRequest request) {

    // 1) Lấy order
    Order order = orderService.getById(orderId)
            .orElseThrow(() -> new RuntimeException("Order không tồn tại: " + orderId));

    // 2) Gán nhân viên nếu chưa có
    if (order.getEmployee() == null) {
        Employee emp = securityUtils.getCurrentEmployee()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên hiện tại"));
        order.setEmployee(emp);
    }

    // 3) Áp dụng promotion nếu có
    if (request.getPromotionId() != null) {
        orderService.applyPromotionToOrder(order, request.getPromotionId());
    } else if (request.getPromotionCode() != null && !request.getPromotionCode().isEmpty()) {
        Long promoId = promotionService.getPromotionByCode(request.getPromotionCode())
                .map(Promotion::getId)
                .orElseThrow(() -> new RuntimeException("Khuyến mãi không tồn tại"));
        orderService.applyPromotionToOrder(order, promoId);
    }

    // 4) Tính tổng tiền nếu chưa có
    if (order.getTotalAmount() == null) {
        order.setTotalAmount(orderService.calculateTotalWithPromotion(order));
    }

    // 5) Gán phương thức thanh toán
    PaymentMethod method = request.getMethod() != null ? request.getMethod() : PaymentMethod.CASH;
    order.setPaymentMethod(method);

    // 6) Tạo Bill trước
    Bill bill = billService.createFromOrder(order);

    // 7) Tạo Payment từ PaymentService
    Payment payment = paymentService.createPayment(
            order.getId(),
            order.getTotalAmount(),
            method,
            request.getBankBin(),
            request.getAccountNumber(),
            request.getOwnerName()
    );

    // 8) Gắn Payment vào Bill và Order
    bill.setPayment(payment);
    bill.setPaymentStatus(payment.getStatus());
    order.setPayment(payment);

    // 9) Lưu Order (có Payment)
    orderService.save(order);

    // 10) Xuất PDF hóa đơn
    byte[] pdfBytes = orderService.exportInvoicePdf(order.getId());

    // 11) Trả về FE
    return ResponseEntity.ok(Map.of(
            "invoicePdfBase64", Base64.getEncoder().encodeToString(pdfBytes),
            "paymentStatus", payment.getStatus(),
            "qrCodeBase64", payment.getQrCodeBase64(),
            "amount", order.getTotalAmount(),
            "employeeId", order.getEmployee() != null ? order.getEmployee().getId() : null
    ));
}

    // =================== CONFIRM ORDER REQUEST ===================
    @PutMapping("/order-requests/{id}/confirm")
    public ResponseEntity<Order> confirmOrderRequest(@PathVariable Long id, @RequestParam Long employeeId) {
        OrderRequest request = orderRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order request không tồn tại"));

        request.setConfirmed(true);
        orderRequestRepository.save(request);

        List<OrderItem> orderItems = request.getItems().stream().map(ri -> {
            OrderItem item = new OrderItem();
            item.setProduct(ri.getProduct());
            item.setQuantity(ri.getQuantity());
            item.setPrice(ri.getProduct().getPrice());
            item.setSubtotal(ri.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(ri.getQuantity())));
            return item;
        }).toList();

        Order order = orderService.createOrderForTable(request.getTable(), orderItems);
        order.setEmployee(employeeRepository.findById(employeeId).orElse(null));
        orderService.updateStatus(order.getId(), OrderStatus.CONFIRMED, employeeId);

        return ResponseEntity.ok(order);
    }
    
}
