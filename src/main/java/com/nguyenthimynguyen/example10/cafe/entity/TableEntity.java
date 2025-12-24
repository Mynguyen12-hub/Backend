package com.nguyenthimynguyen.example10.cafe.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nguyenthimynguyen.example10.cafe.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tables")
public class TableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer number; // Số bàn

    private Integer capacity = 4; // Số ghế mặc định

    @Enumerated(EnumType.STRING)
    private Status status = Status.FREE; // Mặc định bàn trống

    // ✅ thêm để liên kết đặt bàn (reservation)
    @Column(name = "assigned_table_id")
    private Long assignedTableId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "table", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Order> orders; // Danh sách hóa đơn của bàn
}
