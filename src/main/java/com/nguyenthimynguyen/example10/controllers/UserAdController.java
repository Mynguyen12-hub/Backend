package com.nguyenthimynguyen.example10.controllers;

import com.nguyenthimynguyen.example10.dto.UserRequest;
import com.nguyenthimynguyen.example10.model.User;
import com.nguyenthimynguyen.example10.security.services.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userAdController") // đặt tên bean duy nhất
@RequestMapping("/api/admin/user")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3002"})
public class UserAdController {

    @Autowired
    private UserService userService;

    // --- Lấy tất cả user có phân trang ---
    @GetMapping("/all")
    public Page<User> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        return userService.getAllUsers(PageRequest.of(page, size));
    }

    // --- Lấy danh sách nhân viên (ROLE_MODERATOR) ---
    @GetMapping("/employees")
    public Page<User> getEmployees(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        return userService.getUsersByRole("ROLE_MODERATOR", PageRequest.of(page, size));
    }

    // --- Lấy danh sách admin (ROLE_ADMIN) ---
    @GetMapping("/admins")
    public Page<User> getAdmins(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        return userService.getUsersByRole("ROLE_ADMIN", PageRequest.of(page, size));
    }

    // --- Lấy danh sách khách hàng (ROLE_USER) ---
    @GetMapping("/customers")
    public Page<User> getCustomers(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        return userService.getUsersByRole("ROLE_USER", PageRequest.of(page, size));
    }

    // --- Tạo user mới ---
@PostMapping("/create")
public User createUser(@Valid @RequestBody UserRequest request) {
    return userService.createUser(request);
}

@PutMapping("/{id}")
public User updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
    return userService.updateUser(id, request);
}

    // --- Cập nhật roles cho user ---
    @PutMapping("/{id}/roles")
    public User updateUserRoles(@PathVariable Long id, @RequestBody List<String> roles) {
        return userService.updateUserRoles(id, roles);
    }

    // --- Xóa user ---
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
