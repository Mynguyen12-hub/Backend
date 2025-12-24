package com.nguyenthimynguyen.example10.security.services;

import com.nguyenthimynguyen.example10.cafe.entity.Bill;
import com.nguyenthimynguyen.example10.cafe.entity.Order;
import com.nguyenthimynguyen.example10.cafe.entity.Payment;
import com.nguyenthimynguyen.example10.cafe.entity.enums.PaymentMethod;
import com.nguyenthimynguyen.example10.cafe.entity.enums.PaymentStatus;
import com.nguyenthimynguyen.example10.repository.OrderRepository;
import com.nguyenthimynguyen.example10.repository.PaymentRepository;
import com.nguyenthimynguyen.example10.utils.VietQRUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    // Lấy tất cả payment
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Tạo Payment từ Order (đã có Bill)
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
            case CASH:
            case CARD:
                payment.setStatus(PaymentStatus.COMPLETED);
                break;

            case MOBILE:
                payment.setStatus(PaymentStatus.PENDING);
                payment.setMobileCode(generateMobileCode());
                break;

            case BANK:
                payment.setStatus(PaymentStatus.PENDING);
                if (bankBin != null && accountNumber != null && ownerName != null) {
                    try {
                        String qrBase64 = VietQRUtils.createVietQRBase64(bankBin, accountNumber, ownerName, amount.longValue());
                        payment.setQrCodeBase64(qrBase64);
                    } catch (Exception e) {
                        throw new RuntimeException("Lỗi tạo QR VietQR", e);
                    }
                }
                break;
        }

        Payment saved = paymentRepository.save(payment);

        // Gắn Payment vào Order
        order.setPayment(saved);
        order.setPaymentMethod(saved.getMethod());
        orderRepository.save(order);

        return saved;
    }

    private String generateMobileCode() {
        int length = 8;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = (int) (Math.random() * 10);
            sb.append(digit);
        }
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
}
