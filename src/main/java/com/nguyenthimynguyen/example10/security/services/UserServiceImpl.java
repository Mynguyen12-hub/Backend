package com.nguyenthimynguyen.example10.security.services;

import com.nguyenthimynguyen.example10.dto.UserRequest;
import com.nguyenthimynguyen.example10.model.ERole;
import com.nguyenthimynguyen.example10.model.Role;
import com.nguyenthimynguyen.example10.model.User;
import com.nguyenthimynguyen.example10.repository.RoleRepository;
import com.nguyenthimynguyen.example10.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    // Lấy tất cả user theo trang
    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    // Lấy tất cả user không phân trang
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Lấy user theo role và pageable
    @Override
    public Page<User> getUsersByRole(String roleName, Pageable pageable) {
        Optional<Role> role = roleRepository.findByName(ERole.valueOf(roleName));
        if (role.isPresent()) {
            return userRepository.findByRoles(role.get(), pageable);
        }
        return Page.empty();
    }

    // Tạo user mới
    @Override
    public User createUser(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setPassword(encoder.encode(request.getPassword()));

        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null) {
            request.getRoles().forEach(r -> {
                switch (r.toUpperCase()) {
                    case "ADMIN":
                        roles.add(roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Role not found")));
                        break;
                    case "MODERATOR":
                        roles.add(roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Role not found")));
                        break;
                    default:
                        roles.add(roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Role not found")));
                }
            });
        } else {
            roles.add(roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Role not found")));
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }

    // Cập nhật thông tin user
    @Override
    public User updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
if(request.getUsername() != null) user.setUsername(request.getUsername());
if(request.getEmail() != null) user.setEmail(request.getEmail());
if(request.getFullName() != null) user.setFullName(request.getFullName());
if(request.getPhone() != null) user.setPhone(request.getPhone());
if(request.getPassword() != null && !request.getPassword().isEmpty()) {
    user.setPassword(encoder.encode(request.getPassword()));
}
        return userRepository.save(user);
    }

    // Cập nhật roles của user
    @Override
    public User updateUserRoles(Long id, List<String> rolesList) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Role> roles = new HashSet<>();
        if (rolesList != null) {
            rolesList.forEach(r -> {
                switch (r.toUpperCase()) {
                    case "ADMIN":
                        roles.add(roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Role not found")));
                        break;
                    case "MODERATOR":
                        roles.add(roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Role not found")));
                        break;
                    default:
                        roles.add(roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Role not found")));
                }
            });
        }
        user.setRoles(roles);
        return userRepository.save(user);
    }

    // Xóa user
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
