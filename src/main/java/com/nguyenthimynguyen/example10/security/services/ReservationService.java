package com.nguyenthimynguyen.example10.security.services;

import com.nguyenthimynguyen.example10.cafe.entity.Reservation;
import com.nguyenthimynguyen.example10.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository repo;

    public ReservationService(ReservationRepository repo) {
        this.repo = repo;
    }

    public List<Reservation> getAll() {
        return repo.findAll();
    }

    public Optional<Reservation> getById(Long id) {
        return repo.findById(id);
    }

    public Reservation create(Reservation reservation) {
        reservation.setStatus("PENDING"); // mặc định khi khách đặt
        return repo.save(reservation);
    }

    public Reservation confirm(Long id) {
        return repo.findById(id).map(r -> {
            r.setStatus("CONFIRMED");
            return repo.save(r);
        }).orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    public Reservation cancel(Long id) {
        return repo.findById(id).map(r -> {
            r.setStatus("CANCELLED");
            return repo.save(r);
        }).orElseThrow(() -> new RuntimeException("Reservation not found"));
    }
}
