package com.nguyenthimynguyen.example10.controllers;

import com.nguyenthimynguyen.example10.cafe.entity.TableEntity;
import com.nguyenthimynguyen.example10.security.services.TableService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tables")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    // ✅ Lấy danh sách tất cả bàn
    @GetMapping
    public List<TableEntity> getAll() {
        return tableService.getAll();
    }

    // ✅ Lấy bàn theo ID
    @GetMapping("/{id}")
    public TableEntity getById(@PathVariable Long id) {
        return tableService.getById(id)
                .orElseThrow(() -> new RuntimeException("Table not found with id " + id));
    }

    // ✅ Tạo bàn mới
    @PostMapping
    public TableEntity create(@RequestBody TableEntity table) {
        return tableService.create(table);
    }

    // ✅ Cập nhật thông tin bàn
    @PutMapping("/{id}")
    public TableEntity update(@PathVariable Long id, @RequestBody TableEntity table) {
        return tableService.update(id, table);
    }

    // ✅ Xóa bàn
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        tableService.delete(id);
    }

    // ✅ Cập nhật trạng thái bàn (FREE, RESERVED, OCCUPIED, PAID)
    @PutMapping("/{id}/status")
    public TableEntity updateStatus(@PathVariable Long id, @RequestParam String status) {
        return tableService.updateStatus(id, status);
    }

    // ✅ Lọc bàn theo trạng thái
    @GetMapping("/status/{status}")
    public List<TableEntity> getByStatus(@PathVariable String status) {
        return tableService.getByStatus(status);
    }
}
