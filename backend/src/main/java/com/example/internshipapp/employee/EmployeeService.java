package com.example.internshipapp.employee;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internshipapp.common.enums.ContractType;
import com.example.internshipapp.common.enums.EmployeeStatus;
import com.example.internshipapp.common.exception.ResourceNotFoundException;
import com.example.internshipapp.employee.dto.EmployeeRequest;
import com.example.internshipapp.employee.dto.EmployeeResponse;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        if (employeeRepository.existsByRegistrationNumber(request.registrationNumber())) {
            throw new IllegalArgumentException("Employee registration number already exists");
        }

        Employee employee = new Employee();
        fillEmployee(employee, request);
        return toResponse(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAll(String search, EmployeeStatus status) {
        String normalizedSearch = search == null || search.isBlank() ? null : search.trim();
        List<Employee> employees;

        if (normalizedSearch == null && status == null) {
            employees = employeeRepository.findAllByOrderByUpdatedAtDesc();
        } else if (normalizedSearch == null) {
            employees = employeeRepository.findByStatusOrderByUpdatedAtDesc(status);
        } else if (status == null) {
            employees = employeeRepository.search(normalizedSearch);
        } else {
            employees = employeeRepository.searchByStatus(normalizedSearch, status);
        }

        return employees.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findById(Long id) {
        return toResponse(getEmployee(id));
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = getEmployee(id);

        employeeRepository.findByRegistrationNumber(request.registrationNumber())
                .filter(existingEmployee -> !existingEmployee.getId().equals(id))
                .ifPresent(existingEmployee -> {
                    throw new IllegalArgumentException("Employee registration number already exists");
                });

        fillEmployee(employee, request);
        return toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeResponse deactivate(Long id) {
        Employee employee = getEmployee(id);
        employee.setStatus(EmployeeStatus.INACTIVE);
        return toResponse(employeeRepository.save(employee));
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    private void fillEmployee(Employee employee, EmployeeRequest request) {
        employee.setRegistrationNumber(request.registrationNumber());
        employee.setFullName(request.fullName());
        employee.setQualification(request.qualification());
        employee.setContractType(request.contractType() == null ? ContractType.OTHER : request.contractType());
        employee.setHourlyCost(request.hourlyCost());
        employee.setStatus(request.status() == null ? EmployeeStatus.ACTIVE : request.status());
    }

    private EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getRegistrationNumber(),
                employee.getFullName(),
                employee.getQualification(),
                employee.getContractType(),
                employee.getHourlyCost(),
                employee.getStatus(),
                employee.getCreatedAt(),
                employee.getUpdatedAt());
    }
}
