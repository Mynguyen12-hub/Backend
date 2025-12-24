package com.nguyenthimynguyen.example10.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyenthimynguyen.example10.model.ERole;
import com.nguyenthimynguyen.example10.model.Role;
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
    
}
