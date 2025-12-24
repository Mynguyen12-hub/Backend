package com.nguyenthimynguyen.example10.controllers;

import com.nguyenthimynguyen.example10.cafe.entity.Employee;
import com.nguyenthimynguyen.example10.security.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.io.File;

@RestController
@RequestMapping("/api/admin/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.createEmployee(employee);
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return employeeService.updateEmployee(id, employee);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }
    @PostMapping("/upload")
public Employee uploadEmployeeImage(@RequestParam("id") Long id,
                                    @RequestParam("file") MultipartFile file) {
    Employee emp = employeeService.getEmployeeById(id);

    // Lưu file vào thư mục local
    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
    String uploadDir = "src/main/resources/static/images/";
    try {
        File saveFile = new File(uploadDir + fileName);
        file.transferTo(saveFile);
        emp.setImageUrl("/images/" + fileName); // URL để frontend load
        employeeService.updateEmployee(id, emp);
    } catch (Exception e) {
        throw new RuntimeException("Error uploading image: " + e.getMessage());
    }
    return emp;
}

}
