package com.nguyenthimynguyen.example10.controllers.user;

import com.nguyenthimynguyen.example10.cafe.entity.*;
import com.nguyenthimynguyen.example10.cafe.entity.enums.PaymentMethod;
import com.nguyenthimynguyen.example10.cafe.entity.enums.Status;
import com.nguyenthimynguyen.example10.repository.*;
import com.nguyenthimynguyen.example10.security.services.OrderService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Cho phép React hoặc mobile app truy cập
public class UserController {

    private final ProductRepository productRepository;
    private final TableRepository tableRepository;
    private final OrderService orderService;
    private final CategoryRepository categoryRepository;
    private final ReservationRepository reservationRepository;
    private final OrderRequestRepository orderRequestRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ========================== CATEGORY & PRODUCT ==========================
    @GetMapping("/categories")
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/products")
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    // ========================== TABLE APIs ==========================
    @GetMapping("/tables")
    public List<TableEntity> getAllTables() {
        return tableRepository.findAll();
    }

    @GetMapping("/tables/free")
    public List<TableEntity> getFreeTables() {
        return tableRepository.findAll().stream()
                .filter(t -> t.getStatus() != null && t.getStatus().name().equals("FREE"))
                .toList();
    }

    // ✅ Thêm sản phẩm vào bàn (tạo order nếu chưa có)
    @PostMapping("/tables/{tableId}/add")
    public Order addItemToCurrentOrder(@PathVariable Long tableId,
                                       @RequestParam Long productId,
                                       @RequestParam(defaultValue = "1") Integer quantity,
                                       @RequestParam(required = false) Long promotionId,
                                       @RequestParam(required = false) Long employeeId) {
        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Bàn không tồn tại"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        Order order = orderService.addProductToTable(table, product, quantity, employeeId, promotionId);

        messagingTemplate.convertAndSend("/topic/orders", Map.of(
                "tableId", tableId,
                "orders", List.of(order)
        ));

        return order;
    }

    // ✅ Xem đơn hàng của bàn
    @GetMapping("/tables/{tableId}/order")
    public Order getOrderOfTable(@PathVariable Long tableId) {
        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Bàn không tồn tại"));
        return orderService.getCurrentOrderOfTable(table);
    }

    // ✅ Gửi yêu cầu thanh toán
@PostMapping("/public/tables/{tableId}/request-payment")
@CrossOrigin(origins = "*")
public ResponseEntity<?> publicRequestPayment(@PathVariable Long tableId,
                                              @RequestParam PaymentMethod method) {
    TableEntity table = tableRepository.findById(tableId)
            .orElseThrow(() -> new RuntimeException("Bàn không tồn tại"));

    Order order = orderService.getCurrentOrderOfTable(table);
    if (order == null) return ResponseEntity.badRequest().body("Không có đơn hàng.");

    order.setPaymentMethod(method);
    orderService.save(order);

    table.setStatus(Status.BILL_REQUESTED);
    tableRepository.save(table);

    messagingTemplate.convertAndSend("/topic/orders", Map.of(
            "tableId", tableId,
            "order", order,
            "tableStatus", table.getStatus()
    ));

    return ResponseEntity.ok("✅ Đã gửi yêu cầu thanh toán bằng " + method);
}

    // ========================== RESERVATION APIs ==========================
    @GetMapping("/reservations")
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @GetMapping("/reservations/{id}")
    public Reservation getReservationById(@PathVariable Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation không tồn tại"));
    }

    @PostMapping("/reservations")
    public Reservation createReservation(@RequestBody Reservation reservation) {
        if (reservation.getStatus() == null || reservation.getStatus().isEmpty()) {
            reservation.setStatus("PENDING");
        }
        return reservationRepository.save(reservation);
    }

    @PutMapping("/reservations/{id}")
    public Reservation updateReservation(@PathVariable Long id, @RequestBody Reservation resDetails) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation không tồn tại"));

        reservation.setCustomerName(resDetails.getCustomerName());
        reservation.setEmail(resDetails.getEmail());
        reservation.setPhone(resDetails.getPhone());
        reservation.setReservationDate(resDetails.getReservationDate());
        reservation.setReservationTime(resDetails.getReservationTime());
        reservation.setNumPeople(resDetails.getNumPeople());
        reservation.setSpecialRequest(resDetails.getSpecialRequest());

        return reservationRepository.save(reservation);
    }

    // ✅ API đăng nhập bằng thông tin đặt bàn
    @PostMapping("/reservations/login")
    public ResponseEntity<?> loginReservation(@RequestBody Reservation loginRequest) {
        String name = loginRequest.getCustomerName();
        String phone = loginRequest.getPhone();

        if (name == null || name.trim().isEmpty() || phone == null || phone.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vui lòng nhập đầy đủ thông tin!");
        }

        String normalizedName = normalize(name);
        String normalizedPhone = phone.trim();

        return reservationRepository.findAll().stream()
                .filter(r -> r.getCustomerName() != null && r.getPhone() != null)
                .filter(r -> normalize(r.getCustomerName()).equalsIgnoreCase(normalizedName))
                .filter(r -> r.getPhone().trim().equals(normalizedPhone))
                .findFirst()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Thông tin không đúng hoặc không tồn tại!"));
    }

    private String normalize(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("\\s+", " ");
    }

    // ========================== ORDER REQUEST APIs ==========================
    @PostMapping("/order-requests")
    public OrderRequest createOrderRequest(@RequestBody OrderRequest request) {
        request.setConfirmed(false);
        request.setCreatedAt(LocalDateTime.now());
        return orderRequestRepository.save(request);
    }

    @GetMapping("/order-requests/my")
    public List<OrderRequest> getMyOrderRequests(@RequestParam Long tableId) {
        return orderRequestRepository.findByTableId(tableId);
    }
}
