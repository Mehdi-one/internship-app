package com.example.internshipapp.employee;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.internshipapp.common.enums.EmployeeStatus;
import com.example.internshipapp.employee.dto.EmployeeRequest;
import com.example.internshipapp.employee.dto.EmployeeResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public EmployeeResponse create(@Valid @RequestBody EmployeeRequest request) {
        return employeeService.create(request);
    }

    @GetMapping
    public List<EmployeeResponse> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EmployeeStatus status) {
        return employeeService.findAll(search, status);
    }

    @GetMapping("/{id}")
    public EmployeeResponse findById(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return employeeService.update(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    public EmployeeResponse deactivate(@PathVariable Long id) {
        return employeeService.deactivate(id);
    }
}
