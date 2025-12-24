package com.nguyenthimynguyen.example10.security.services;

import com.nguyenthimynguyen.example10.dto.UserRequest;
import com.nguyenthimynguyen.example10.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    Page<User> getAllUsers(Pageable pageable);
    Page<User> getUsersByRole(String role, Pageable pageable);
    User createUser(UserRequest request);
    User updateUser(Long id, UserRequest request);
    User updateUserRoles(Long id, List<String> roles);
    void deleteUser(Long id);
    List<User> getAllUsers(); // nếu muốn danh sách không phân trang
}
