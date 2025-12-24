package com.nguyenthimynguyen.example10.controllers.employee;

import com.nguyenthimynguyen.example10.cafe.entity.Reservation;
import com.nguyenthimynguyen.example10.security.services.ReservationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/reservations")
public class EmployeeReservationController {

    private final ReservationService service;

    public EmployeeReservationController(ReservationService service) {
        this.service = service;
    }

    // Xem tất cả reservation
    @GetMapping
    public List<Reservation> getAll() {
        return service.getAll();
    }

    // Xem chi tiết reservation
    @GetMapping("/{id}")
    public Reservation getById(@PathVariable Long id) {
        return service.getById(id).orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    // Xác nhận reservation (chọn bàn)
    @PutMapping("/{id}/confirm")
    public Reservation confirm(@PathVariable Long id) {
        return service.confirm(id);
    }

    // Hủy reservation
    @PutMapping("/{id}/cancel")
    public Reservation cancel(@PathVariable Long id) {
        return service.cancel(id);
    }
}
