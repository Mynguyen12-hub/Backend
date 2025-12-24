package com.nguyenthimynguyen.example10.repository;

import com.nguyenthimynguyen.example10.cafe.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByReservationDate(LocalDate date);
@Query("SELECT r FROM Reservation r WHERE " +
       "LOWER(REPLACE(TRIM(r.customerName), '  ', ' ')) = LOWER(REPLACE(TRIM(:customerName), '  ', ' ')) " +
       "AND r.phone = :phone")
Optional<Reservation> findByCustomerNameAndPhone(
        @Param("customerName") String customerName,
        @Param("phone") String phone);

}
