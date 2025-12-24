package com.nguyenthimynguyen.example10.security.services;

import com.nguyenthimynguyen.example10.cafe.entity.Order;
import com.nguyenthimynguyen.example10.cafe.entity.OrderItem;
import com.nguyenthimynguyen.example10.exception.NotFoundException;
import com.nguyenthimynguyen.example10.repository.OrderItemRepository;
import com.nguyenthimynguyen.example10.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {

    private final OrderItemRepository repo;
    private final OrderRepository orderRepo;

    public OrderItemService(OrderItemRepository repo, OrderRepository orderRepo) {
        this.repo = repo;
        this.orderRepo = orderRepo;
    }

    public List<OrderItem> getAll() {
        return repo.findAll();
    }

    public Optional<OrderItem> getById(Long id) {
        return repo.findById(id);
    }

    public OrderItem create(OrderItem item) {
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());

        if (item.getPrice() != null && item.getQuantity() != null) {
            item.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        return repo.save(item);
    }

    public OrderItem update(Long id, OrderItem item) {
        return repo.findById(id).map(existing -> {
            existing.setProduct(item.getProduct());
            existing.setQuantity(item.getQuantity());
            existing.setPrice(item.getPrice());

            if (existing.getPrice() != null && existing.getQuantity() != null) {
                existing.setSubtotal(existing.getPrice().multiply(BigDecimal.valueOf(existing.getQuantity())));
            } else {
                existing.setSubtotal(BigDecimal.ZERO);
            }

            existing.setUpdatedAt(LocalDateTime.now());
            return repo.save(existing);
        }).orElseThrow(() -> new RuntimeException("OrderItem not found with id " + id));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    /** Thêm món vào order */
    public OrderItem addItemToOrder(Long orderId, OrderItem item) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with id " + orderId));

        item.setOrder(order); // gán order cho item
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());

        if (item.getPrice() != null && item.getQuantity() != null) {
            item.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        return repo.save(item);
    }
}
