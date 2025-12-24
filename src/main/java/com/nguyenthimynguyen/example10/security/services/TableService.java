package com.nguyenthimynguyen.example10.security.services;

import com.nguyenthimynguyen.example10.cafe.entity.TableEntity;
import com.nguyenthimynguyen.example10.cafe.entity.enums.Status;
import com.nguyenthimynguyen.example10.repository.TableRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TableService {

    private final TableRepository tableRepository;

    public TableService(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    // ✅ Lấy toàn bộ bàn
    public List<TableEntity> getAll() {
        return tableRepository.findAll();
    }

    // ✅ Lấy bàn theo ID
    public Optional<TableEntity> getById(Long id) {
        return tableRepository.findById(id);
    }

    // ✅ Tạo bàn mới
    public TableEntity create(TableEntity table) {
        table.setCreatedAt(LocalDateTime.now());
        table.setUpdatedAt(LocalDateTime.now());
        if (table.getStatus() == null) {
            table.setStatus(Status.FREE);
        }
        return tableRepository.save(table);
    }

    // ✅ Cập nhật thông tin bàn
    public TableEntity update(Long id, TableEntity table) {
        return tableRepository.findById(id).map(existing -> {
            existing.setNumber(table.getNumber());
            existing.setCapacity(table.getCapacity());
            existing.setStatus(table.getStatus());
            existing.setUpdatedAt(LocalDateTime.now());
            return tableRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Table not found with id " + id));
    }

    // ✅ Xóa bàn
    public void delete(Long id) {
        tableRepository.deleteById(id);
    }

    // ✅ Lọc bàn theo trạng thái (FREE, OCCUPIED, RESERVED, PAID)
    public List<TableEntity> getByStatus(String statusStr) {
        Status status;
        try {
            status = Status.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status");
        }

        return tableRepository.findAll().stream()
                .filter(table -> table.getStatus() == status)
                .collect(Collectors.toList());
    }

    // ✅ Cập nhật trạng thái bàn
    public TableEntity updateStatus(Long id, String statusStr) {
        return tableRepository.findById(id).map(table -> {
            Status status;
            try {
                status = Status.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid status");
            }

            table.setStatus(status);
            table.setUpdatedAt(LocalDateTime.now());
            return tableRepository.save(table);
        }).orElseThrow(() -> new RuntimeException("Table not found with id " + id));
    }
}
