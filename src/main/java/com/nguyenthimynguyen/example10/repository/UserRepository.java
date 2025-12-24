package com.nguyenthimynguyen.example10.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyenthimynguyen.example10.model.User;
import com.nguyenthimynguyen.example10.model.ERole;
import com.nguyenthimynguyen.example10.model.Role;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Page<User> findByRoles_Name(ERole roleName, Pageable pageable);
Page<User> findByRoles(Role role, Pageable pageable);
    }
