package com.nguyenthimynguyen.example10.cafe.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nguyenthimynguyen.example10.cafe.entity.enums.PaymentMethod;
import com.nguyenthimynguyen.example10.cafe.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING; // mặc định PENDING

    @OneToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
@Column(name = "qr_code_base64", columnDefinition = "TEXT")
private String qrCodeBase64; // dùng để lưu Base64 QR code
    // Constructor mặc định
    public Payment() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Tự động set thời gian khi insert/update
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    @Column(name = "mobile_code", unique = true)
private String mobileCode; // chỉ dùng cho phương thức MOBILE

// getter & setter
public String getMobileCode() { return mobileCode; }
public void setMobileCode(String mobileCode) { this.mobileCode = mobileCode; }
// getter & setter
public String getQrCodeBase64() {
    return qrCodeBase64;
}

public void setQrCodeBase64(String qrCodeBase64) {
    this.qrCodeBase64 = qrCodeBase64;
}
}
