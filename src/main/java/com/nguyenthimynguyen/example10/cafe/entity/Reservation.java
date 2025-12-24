package com.nguyenthimynguyen.example10.cafe.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    private String email;
    private String phone;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    // Cho phép frontend gửi "HH:mm" thay vì "HH:mm:ss"
    @JsonFormat(pattern = "HH:mm")
    @Column(name = "reservation_time", nullable = false)
    private LocalTime reservationTime;

    @Column(name = "num_people", nullable = false)
    private int numPeople;

    @Column(name = "special_request")
    private String specialRequest;

    @Column(name = "status", nullable = false)
    private String status;

    // ✅ Bổ sung liên kết đến bàn được gán
    @Column(name = "assigned_table_id")
    private Long assignedTableId;

    // ===== Getter & Setter =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public int getNumPeople() {
        return numPeople;
    }

    public void setNumPeople(int numPeople) {
        this.numPeople = numPeople;
    }

    public String getSpecialRequest() {
        return specialRequest;
    }

    public void setSpecialRequest(String specialRequest) {
        this.specialRequest = specialRequest;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getAssignedTableId() {
        return assignedTableId;
    }

    public void setAssignedTableId(Long assignedTableId) {
        this.assignedTableId = assignedTableId;
    }
}
