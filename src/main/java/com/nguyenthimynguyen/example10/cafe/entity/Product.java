package com.nguyenthimynguyen.example10.cafe.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Lob
    private String description;

    @Column(nullable = false , precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "category_id")   // đổi lại cho đúng
    private Category category;

    private String imageUrl;
    private Integer stockQuantity = 0;
    private Boolean isActive = true;

    @CreationTimestamp   // Hibernate tự set khi INSERT
    private LocalDateTime createdAt;

    @UpdateTimestamp     // Hibernate tự update khi UPDATE
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "products")
    private Set<Promotion> promotions;
}
