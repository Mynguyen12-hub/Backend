package com.nguyenthimynguyen.example10.security.services;
import java.util.Base64;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nguyenthimynguyen.example10.cafe.entity.*;
import com.nguyenthimynguyen.example10.cafe.entity.enums.OrderStatus;
import com.nguyenthimynguyen.example10.cafe.entity.enums.PaymentMethod;
import com.nguyenthimynguyen.example10.cafe.entity.enums.PaymentStatus;
import com.nguyenthimynguyen.example10.dto.OrderDTO;
import com.nguyenthimynguyen.example10.exception.InvalidStatusTransitionException;
import com.nguyenthimynguyen.example10.exception.NotFoundException;
import com.nguyenthimynguyen.example10.repository.*;
import com.nguyenthimynguyen.example10.security.SecurityUtils;
import com.nguyenthimynguyen.example10.utils.VietQRUtils;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.nguyenthimynguyen.example10.cafe.entity.enums.Status;       // trạng thái bàn
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

private final SecurityUtils securityUtils;
private final OrderRepository orderRepo;
private final ProductRepository productRepo;
private final TableRepository tableRepo;
private final EmployeeRepository employeeRepo;
private final PaymentRepository paymentRepository;
private final OrderItemRepository orderItemRepository;
private final PromotionRepository promotionRepository;
private final PromotionService promotionService; 
private final BillRepository billRepository; // thêm BillRepository
private final OrderRepository orderRepository;
private final BillService billService;

public OrderService(
        OrderRepository orderRepo,
        OrderRepository orderRepository,
        ProductRepository productRepo,
        TableRepository tableRepo,
        EmployeeRepository employeeRepo,
        SecurityUtils securityUtils,
        PaymentRepository paymentRepository,
        PromotionRepository promotionRepository,
        PromotionService promotionService,
        OrderItemRepository orderItemRepository,
        BillRepository billRepository,
        BillService billService  // inject BillRepository
) {
    this.orderRepo = orderRepo;
    this.orderRepository = orderRepository;
    this.productRepo = productRepo;
    this.tableRepo = tableRepo;
    this.employeeRepo = employeeRepo;
    this.paymentRepository = paymentRepository;
    this.orderItemRepository = orderItemRepository;
    this.promotionRepository = promotionRepository;
    this.promotionService = promotionService;
    this.securityUtils = securityUtils;
    this.billRepository = billRepository;
    this.billService = billService; // gán
}

    // =================== BILL ===================

    // Lấy bill theo orderId
    public Optional<Bill> getBillByOrderId(Long orderId) {
        return billRepository.findByOrderId(orderId);
    }

    // Tạo bill cho order
    public Bill createBillForOrder(Order order, BigDecimal totalAmount, PaymentMethod method) {
        Bill bill = new Bill();
        bill.setOrder(order);
        bill.setTotalAmount(totalAmount);
        bill.setPaymentMethod(method);
        bill.setPaymentStatus(PaymentStatus.PENDING);
        bill.setIssuedAt(LocalDateTime.now());
        bill.setCreatedAt(LocalDateTime.now());
        bill.setUpdatedAt(LocalDateTime.now());
        return billRepository.save(bill);
    }

    // Cập nhật bill
    public Bill updateBill(Long billId, Bill updatedBill) {
        return billRepository.findById(billId).map(existing -> {
            existing.setTotalAmount(updatedBill.getTotalAmount());
            existing.setPaymentMethod(updatedBill.getPaymentMethod());
            existing.setPaymentStatus(updatedBill.getPaymentStatus());
            existing.setIssuedAt(updatedBill.getIssuedAt());
            existing.setNotes(updatedBill.getNotes());
            existing.setUpdatedAt(LocalDateTime.now());
            return billRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Bill không tồn tại: " + billId));
    }

public List<OrderDTO> getAllOrdersDTO() {
    return orderRepo.findAllWithDetails() // fetch table, employee, items, product
                    .stream()
                    .map(OrderDTO::new)
                    .collect(Collectors.toList());
}
    // ------------------ TABLE ------------------
    public Optional<TableEntity> getTableById(Long id) {
        return tableRepo.findById(id);
    }

    public Order save(Order order) {
        return orderRepo.save(order);
    }
// Lấy Bill theo Order
    public Optional<Bill> getBillByOrder(Order order) {
        return billRepository.findByOrderId(order.getId());
    }
    // ------------------ PRODUCT ------------------
    public Optional<Product> getProductById(Long id) {
        return productRepo.findById(id);
    }

    // ------------------ ORDER ------------------
    public List<Order> getAll() {
        return orderRepo.findAll();
    }

    public Optional<Order> getById(Long id) {
        return orderRepo.findById(id);
    }

@Transactional
    public Order createOrderForTable(TableEntity table, List<OrderItem> items) {
        Order order = new Order();
        order.setTable(table);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setItems(new ArrayList<>());

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem i : items) {
            Product p = productRepo.findById(i.getProduct().getId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + i.getProduct().getId()));
            i.setPrice(p.getPrice());
            i.setSubtotal(p.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())));
            i.setOrder(order);
            i.setCreatedAt(LocalDateTime.now());
            i.setUpdatedAt(LocalDateTime.now());
            order.getItems().add(i);
            total = total.add(i.getSubtotal());
            orderItemRepository.save(i);
        }

        order.setTotalAmount(total);
        return orderRepo.save(order);
    }
public Order updateStatus(Long orderId, OrderStatus nextStatus, Long employeeId) {
    Order order = orderRepo.findById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

    if (!isValidStatusTransition(order.getStatus(), nextStatus)) {
        throw new InvalidStatusTransitionException(
                "Cannot change status from " + order.getStatus() + " to " + nextStatus
        );
    }

    order.setStatus(nextStatus);
    order.setUpdatedAt(LocalDateTime.now());

    if (employeeId != null) {
        employeeRepo.findById(employeeId).ifPresent(order::setEmployee);
    }

    return orderRepo.save(order);
}

    private boolean isValidStatusTransition(OrderStatus current, OrderStatus next) {
        return switch (current) {
            case PENDING -> next == OrderStatus.CONFIRMED;
            case CONFIRMED -> next == OrderStatus.PREPARING;
            case PREPARING -> next == OrderStatus.SERVED;
            case SERVED -> next == OrderStatus.PAID;
            case PAID, CANCELLED -> false;
        };
    }

    public void delete(Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));
        orderRepo.delete(order);
    }

    // ------------------ PAYMENT ------------------
@Transactional
    public Payment createPayment(Long orderId,
                                 BigDecimal amount,
                                 PaymentMethod method,
                                 String bankBin,
                                 String accountNumber,
                                 String ownerName) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setMethod(method);

        switch (method) {
            case CASH, CARD -> payment.setStatus(PaymentStatus.COMPLETED);
            case MOBILE -> {
                payment.setStatus(PaymentStatus.PENDING);
                payment.setMobileCode(generateMobileCode());
            }
case BANK -> {
    payment.setStatus(PaymentStatus.PENDING);

    if (bankBin != null && !bankBin.isBlank() &&
        accountNumber != null && !accountNumber.isBlank() &&
        ownerName != null && !ownerName.isBlank()) {

        try {
            long amt = (amount != null) ? amount.longValue() : 0L;
            String qrBase64 = VietQRUtils.createVietQRBase64(bankBin, accountNumber, ownerName, amt);
            payment.setQrCodeBase64(qrBase64);
        } catch (Exception e) {
            e.printStackTrace();
            // Optional: set trạng thái lỗi hoặc log
            payment.setStatus(PaymentStatus.FAILED);
        }
    } else {
        // Optional: nếu thiếu thông tin bank thì mark payment là lỗi
        payment.setStatus(PaymentStatus.FAILED);
    }
}
        }
        return paymentRepository.save(payment);
    }

    private String generateMobileCode() {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<8;i++) sb.append((int)(Math.random()*10));
        return sb.toString();
    }

    @Transactional
    public Payment updatePaymentStatus(Long paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment không tồn tại cho orderId: " + orderId));
    }
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Order> getCurrentOrdersOfTable(TableEntity table) {
        return orderRepo.findAllByTableAndStatus(table, OrderStatus.PENDING);
    }


    // ------------------ PDF ------------------
    public byte[] exportInvoicePdf(Long orderId, PaymentMethod method, String paymentCode) {
        try {
            Order order = getById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // --- Logo ---
            try {
                Image logo = new Image(ImageDataFactory.create(getClass().getResource("/logo.png")));
                logo.setWidth(100);
                document.add(logo);
            } catch (Exception e) {}

            // --- Header ---
            Paragraph header = new Paragraph("HÓA ĐƠN THANH TOÁN")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(header);
            document.add(new Paragraph("Order ID: " + order.getId()));
            document.add(new Paragraph("Bàn: " + order.getTable().getNumber()));
            document.add(new Paragraph("Nhân viên: " + (order.getEmployee() != null ? order.getEmployee().getUsername() : "Chưa có")));
            document.add(new Paragraph("\n"));

            // --- Bảng sản phẩm ---
            Table table = new Table(UnitValue.createPercentArray(new float[]{4,1,2,2})).useAllAvailableWidth();
            table.addHeaderCell(new Cell().add(new Paragraph("Sản phẩm").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("SL").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Đơn giá").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Thành tiền").setBold()));

            BigDecimal total = BigDecimal.ZERO;
            for (OrderItem item : order.getItems()) {
                BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(subtotal);
                table.addCell(new Cell().add(new Paragraph(item.getProduct().getName())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
                table.addCell(new Cell().add(new Paragraph(item.getPrice().toString())));
                table.addCell(new Cell().add(new Paragraph(subtotal.toString())));
            }
            document.add(table);

            document.add(new Paragraph("\n"));
            document.add(new Paragraph("TỔNG: " + total + " VNĐ")
                    .setBold()
                    .setFontSize(14)
                    .setFontColor(ColorConstants.RED));
            document.add(new Paragraph("Phương thức thanh toán: " + method));

            // QR code cho MOBILE
            if (method == PaymentMethod.MOBILE && paymentCode != null) {
                document.add(new Paragraph("Mã thanh toán: " + paymentCode));
                try {
                    QRCodeWriter qrWriter = new QRCodeWriter();
                    BitMatrix bitMatrix = qrWriter.encode(paymentCode, BarcodeFormat.QR_CODE, 150, 150);
                    BufferedImage qrImage = new BufferedImage(150,150,BufferedImage.TYPE_INT_RGB);
                    for(int x=0;x<150;x++){
                        for(int y=0;y<150;y++){
                            qrImage.setRGB(x,y, bitMatrix.get(x,y) ? 0xFF000000 : 0xFFFFFFFF);
                        }
                    }
                    ByteArrayOutputStream qrBaos = new ByteArrayOutputStream();
                    ImageIO.write(qrImage,"PNG",qrBaos);
                    Image qr = new Image(ImageDataFactory.create(qrBaos.toByteArray()));

                    // Căn giữa QR code
                    Paragraph p = new Paragraph();
                    p.add(qr);
                    p.setTextAlignment(TextAlignment.CENTER);
                    document.add(p);
                } catch(Exception e){ e.printStackTrace(); }
            }

            document.close();
            return baos.toByteArray();
        } catch(Exception e) {
            throw new RuntimeException("Lỗi xuất PDF: "+e.getMessage(), e);
        }
    }
public void applyPromotionToOrder(Order order, Long promotionId) {
    if (promotionId == null) {
        order.setPromotion(null);
        recalcTotal(order);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepo.save(order);
        return;
    }
    Promotion promo = promotionRepository.findById(promotionId)
            .orElseThrow(() -> new NotFoundException("Promotion not found: " + promotionId));
    order.setPromotion(promo);
    recalcTotal(order);
    order.setUpdatedAt(LocalDateTime.now());
    orderRepo.save(order);
}

private void recalcTotal(Order order) {
    BigDecimal total = order.getItems().stream()
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    Promotion promo = order.getPromotion();
    if (promo != null) {
        if (promo.getDiscountPercentage() != null) {
            total = total.multiply(BigDecimal.ONE.subtract(promo.getDiscountPercentage().divide(BigDecimal.valueOf(100))));
        } else if (promo.getDiscountAmount() != null) {
            total = total.subtract(promo.getDiscountAmount());
        }
    }

    order.setTotalAmount(total.max(BigDecimal.ZERO));
}
// Tính tổng tiền của order với promotion, không cập nhật trực tiếp order
public BigDecimal calculateTotalWithPromotion(Order order) {
    BigDecimal total = order.getItems().stream()
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    Promotion promo = order.getPromotion();
    if (promo != null) {
        if (promo.getDiscountPercentage() != null) {
            total = total.multiply(
                    BigDecimal.ONE.subtract(
                            promo.getDiscountPercentage().divide(BigDecimal.valueOf(100))
                    )
            );
        } else if (promo.getDiscountAmount() != null) {
            total = total.subtract(promo.getDiscountAmount());
        }
    }

    return total.max(BigDecimal.ZERO);
}

    // ------------------ THÊM SẢN PHẨM ------------------
@Transactional
public Order addProductToTable(TableEntity table, Product product, int quantity, Long employeeId, Long promotionId) {

    // 1) Cập nhật trạng thái bàn nếu đang FREE
    if (table.getStatus() == null || table.getStatus() == Status.FREE) {
        table.setStatus(Status.OCCUPIED);
        table.setUpdatedAt(LocalDateTime.now());
        tableRepo.save(table);
    }

    // 2) Lấy order PENDING hiện tại hoặc tạo mới
    Order order = orderRepo.findByTableAndStatusWithItems(table, OrderStatus.PENDING)
            .orElseGet(() -> {
                Order o = new Order();
                o.setTable(table);
                o.setStatus(OrderStatus.PENDING);
                o.setCreatedAt(LocalDateTime.now());
                o.setUpdatedAt(LocalDateTime.now());
                o.setItems(new ArrayList<>());
                o.setTotalAmount(BigDecimal.ZERO);
                return orderRepo.save(o);
            });

    if (order.getItems() == null) order.setItems(new ArrayList<>());

    // 3) Thêm hoặc cập nhật item
    Optional<OrderItem> existingOpt = order.getItems().stream()
            .filter(i -> i.getProduct() != null && i.getProduct().getId().equals(product.getId()))
            .findFirst();

    OrderItem item;
    if (existingOpt.isPresent()) {
        item = existingOpt.get();
        item.setQuantity(item.getQuantity() + quantity);
    } else {
        item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        order.getItems().add(item);
    }

    item.setPrice(product.getPrice());
    item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
    item.setUpdatedAt(LocalDateTime.now());
    orderItemRepository.save(item);

    // 4) Áp dụng promotion nếu có
    applyPromotionToOrder(order, promotionId);

    // 5) Set employee & updatedAt
    order.setUpdatedAt(LocalDateTime.now());
Employee emp = null;

if (employeeId != null) {
    emp = employeeRepo.findById(employeeId).orElse(null);
}

if (emp == null) {
    emp = securityUtils.getCurrentEmployee().orElse(null);
}

if (emp != null) {
    order.setEmployee(emp);
}

    return orderRepo.save(order);
}

    public Order advanceOrderStatus(Long orderId, OrderStatus nextStatus, Long employeeId) {
        return updateStatus(orderId, nextStatus, employeeId);
    }

    public Order getCurrentOrderOfTable(TableEntity table) {
        return orderRepo.findByTableAndStatus(table, OrderStatus.PENDING)
                .orElseGet(() -> {
                    Order newOrder = new Order();
                    newOrder.setTable(table);
                    newOrder.setStatus(OrderStatus.PENDING);
                    newOrder.setCreatedAt(LocalDateTime.now());
                    newOrder.setUpdatedAt(LocalDateTime.now());
                    newOrder.setTotalAmount(BigDecimal.ZERO);
                    newOrder.setItems(new ArrayList<>());
                    return orderRepo.save(newOrder);
                });
    }
    public byte[] exportInvoicePdf(Long orderId) {
        getById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));

    Payment payment = getPaymentByOrderId(orderId);
    PaymentMethod method = payment != null ? payment.getMethod() : null;
    String code = payment != null ? payment.getMobileCode() : null;

    return exportInvoicePdf(orderId, method, code); // gọi phương thức export chính
}
// Checkout order: áp dụng promotion, tạo bill, payment, xuất PDF
@Transactional
public Map<String, Object> checkoutOrder(Order order, PaymentMethod method, Long promotionId, String promotionCode) {
    // 1) Áp dụng promotion
    if (promotionId != null) {
        applyPromotionToOrder(order, promotionId);
    } else if (promotionCode != null && !promotionCode.isEmpty()) {
        Long promoId = promotionService.getPromotionByCode(promotionCode)
                .map(Promotion::getId)
                .orElseThrow(() -> new RuntimeException("Khuyến mãi không tồn tại"));
        applyPromotionToOrder(order, promoId);
    }

    // 2) Tính tổng tiền
    if (order.getTotalAmount() == null) {
        order.setTotalAmount(calculateTotalWithPromotion(order));
    }

    // 3) Gán phương thức thanh toán nếu chưa có
    if (order.getPaymentMethod() == null) {
        order.setPaymentMethod(method != null ? method : PaymentMethod.CASH);
    }

    // 4) Gán nhân viên nếu chưa có
    if (order.getEmployee() == null) {
        Employee emp = securityUtils.getCurrentEmployee()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên hiện tại"));
        order.setEmployee(emp);
    }

    // 5) Tạo Bill và Payment
    Bill bill = billService.createFromOrder(order);

    // 6) Lưu order
    orderRepo.save(order);

    // 7) Xuất PDF hóa đơn
    byte[] pdfBytes = exportInvoicePdf(order.getId());

    // 8) Trả về kết quả
    return Map.of(
            "invoicePdfBase64", Base64.getEncoder().encodeToString(pdfBytes),
            "paymentStatus", bill.getPayment().getStatus(),
            "paymentCode", bill.getPayment().getMobileCode(),      // MOBILE
            "qrCodeBase64", bill.getPayment().getQrCodeBase64(),   // BANK
            "amount", bill.getTotalAmount(),
            "employeeId", order.getEmployee().getId()
    );
}

 @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopSellingProducts(int topN) {
        List<Object[]> rows = orderRepo.findTopSellingProducts(OrderStatus.PAID, PageRequest.of(0, topN));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] r : rows) {
            Map<String, Object> m = new java.util.HashMap<>();
            // r[0]=productId, r[1]=productName, r[2]=quantity
            if (r[0] instanceof Number) m.put("productId", ((Number) r[0]).longValue()); else m.put("productId", r[0]);
            m.put("productName", r[1]);
            if (r[2] instanceof Number) m.put("quantitySold", ((Number) r[2]).longValue()); else m.put("quantitySold", r[2]);
            result.add(m);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRevenueByCategory() {
        List<Object[]> rows = orderRepo.findRevenueByCategory(OrderStatus.PAID);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] r : rows) {
            Map<String, Object> m = new java.util.HashMap<>();
            m.put("category", r[0]);
            m.put("revenue", r[1]);
            result.add(m);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRevenueByDay() {
        List<Object[]> rows = orderRepo.findRevenueByDay(OrderStatus.PAID);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] r : rows) {
            Map<String, Object> m = new java.util.HashMap<>();
            m.put("date", r[0]);
            m.put("revenue", r[1]);
            result.add(m);
        }
        return result;
    }
    
}
