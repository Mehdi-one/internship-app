package com.example.internshipapp.assignment;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import com.example.internshipapp.assignment.dto.EmployeeAssignmentRequest;
import com.example.internshipapp.assignment.dto.EmployeeAssignmentResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class EmployeeAssignmentController {

    private final EmployeeAssignmentService employeeAssignmentService;

    public EmployeeAssignmentController(EmployeeAssignmentService employeeAssignmentService) {
        this.employeeAssignmentService = employeeAssignmentService;
    }

    @PostMapping("/projects/{projectId}/employee-assignments")
    public EmployeeAssignmentResponse create(
            @PathVariable Long projectId,
            @Valid @RequestBody EmployeeAssignmentRequest request) {
        return employeeAssignmentService.create(projectId, request);
    }

    @GetMapping("/projects/{projectId}/employee-assignments")
    public List<EmployeeAssignmentResponse> findByProject(@PathVariable Long projectId) {
        return employeeAssignmentService.findByProject(projectId);
    }

    @PutMapping("/employee-assignments/{id}")
    public EmployeeAssignmentResponse update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeAssignmentRequest request) {
        return employeeAssignmentService.update(id, request);
    }

    @PatchMapping("/employee-assignments/{id}/validate")
    public EmployeeAssignmentResponse validate(@PathVariable Long id) {
        return employeeAssignmentService.validate(id);
    }

    @PatchMapping("/employee-assignments/{id}/cancel")
    public EmployeeAssignmentResponse cancel(@PathVariable Long id) {
        return employeeAssignmentService.cancel(id);
    }
}
