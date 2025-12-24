package com.nguyenthimynguyen.example10.repository;

import com.nguyenthimynguyen.example10.cafe.entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRepository extends JpaRepository<TableEntity, Long> {
}
