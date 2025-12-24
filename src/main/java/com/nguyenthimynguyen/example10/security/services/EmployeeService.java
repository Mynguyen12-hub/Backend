package com.nguyenthimynguyen.example10.security.services;

import com.nguyenthimynguyen.example10.cafe.entity.Employee;
import com.nguyenthimynguyen.example10.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public Employee createEmployee(Employee employee) {
        if (employeeRepository.existsByUsername(employee.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee employee) {
        Employee existing = getEmployeeById(id);
        existing.setFullName(employee.getFullName());
        existing.setPhone(employee.getPhone());
        existing.setEmail(employee.getEmail());
        existing.setRole(employee.getRole());
        existing.setImageUrl(employee.getImageUrl());
        existing.setIsActive(employee.getIsActive());
        return employeeRepository.save(existing);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}
