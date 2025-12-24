package com.nguyenthimynguyen.example10.security;

import com.nguyenthimynguyen.example10.cafe.entity.Employee;
import com.nguyenthimynguyen.example10.repository.EmployeeRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtils {

    private final EmployeeRepository employeeRepo;

    public SecurityUtils(EmployeeRepository employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    public Optional<Employee> getCurrentEmployee() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName();
            return employeeRepo.findByUsername(username);
        }
        return Optional.empty();
    }
}
