package com.nguyenthimynguyen.example10.security.services;

import com.nguyenthimynguyen.example10.cafe.entity.Category;
import com.nguyenthimynguyen.example10.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class CategoryService {

    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    public List<Category> getAll() {
        return repo.findAll();
    }

    public Optional<Category> getById(Long id) {
        return repo.findById(id);
    }

public Category create(Category category) {
    category.setCreatedAt(LocalDateTime.now());
    category.setUpdatedAt(LocalDateTime.now());
    return repo.save(category);
}

public Category update(Long id, Category category) {
    return repo.findById(id).map(c -> {
        c.setName(category.getName());
        c.setDescription(category.getDescription());
        c.setImageUrl(category.getImageUrl());
        c.setUpdatedAt(LocalDateTime.now()); // cập nhật thời gian sửa
        return repo.save(c);
    }).orElseThrow(() -> new RuntimeException("Category not found"));
}

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
