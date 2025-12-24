package com.nguyenthimynguyen.example10.controllers;

import com.nguyenthimynguyen.example10.cafe.entity.Bill;
import com.nguyenthimynguyen.example10.repository.BillRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bills")
public class BillController {

    private final BillRepository billRepository;
    
    public BillController(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    // GET all
    @GetMapping
    public List<Bill> getAll() {
        return billRepository.findAll();
    }

    // GET by id
    @GetMapping("/{id}")
    public Bill getById(@PathVariable Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found with id " + id));
    }

    // POST (create)
    @PostMapping
    public Bill create(@RequestBody Bill bill) {
        return billRepository.save(bill);
    }

    // PUT (update)
    @PutMapping("/{id}")
    public Bill update(@PathVariable Long id, @RequestBody Bill bill) {
        Bill existing = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found with id " + id));

        existing.setTotalAmount(bill.getTotalAmount());
        existing.setPaymentMethod(bill.getPaymentMethod());
        existing.setPaymentStatus(bill.getPaymentStatus());
        existing.setIssuedAt(bill.getIssuedAt());
        existing.setNotes(bill.getNotes());
        existing.setUpdatedAt(bill.getUpdatedAt());

        return billRepository.save(existing);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        billRepository.deleteById(id);
    }
}
