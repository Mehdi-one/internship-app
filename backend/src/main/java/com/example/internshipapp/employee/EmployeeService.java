package com.example.internshipapp.employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internshipapp.common.enums.EmployeeStatus;
import com.example.internshipapp.common.exception.ResourceNotFoundException;
import com.example.internshipapp.employee.dto.EmployeeCostHistoryResponse;
import com.example.internshipapp.employee.dto.EmployeeDetailResponse;
import com.example.internshipapp.employee.dto.EmployeeRequest;
import com.example.internshipapp.employee.dto.EmployeeResponse;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeCostHistoryRepository costHistoryRepository;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            EmployeeCostHistoryRepository costHistoryRepository) {
        this.employeeRepository = employeeRepository;
        this.costHistoryRepository = costHistoryRepository;
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        if (employeeRepository.existsByMatricule(request.matricule())) {
            throw new IllegalArgumentException("Employee matricule already exists");
        }

        Employee employee = new Employee();
        fillEmployee(employee, request);
        Employee savedEmployee = employeeRepository.save(employee);
        costHistoryRepository.save(newCostHistory(savedEmployee, savedEmployee.getHourlyCost(), LocalDate.now()));
        return toResponse(savedEmployee);
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

        return employees.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeDetailResponse findById(Long id) {
        Employee employee = getEmployee(id);
        return toDetailResponse(employee, findCostHistoryEntities(id));
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = getEmployee(id);
        BigDecimal previousHourlyCost = employee.getHourlyCost();

        employeeRepository.findByMatricule(request.matricule())
                .filter(existingEmployee -> !existingEmployee.getId().equals(id))
                .ifPresent(existingEmployee -> {
                    throw new IllegalArgumentException("Employee matricule already exists");
                });

        fillEmployee(employee, request);
        Employee savedEmployee = employeeRepository.save(employee);

        if (previousHourlyCost.compareTo(savedEmployee.getHourlyCost()) != 0) {
            costHistoryRepository.save(newCostHistory(
                    savedEmployee,
                    savedEmployee.getHourlyCost(),
                    LocalDate.now()));
        }

        return toResponse(savedEmployee);
    }

    @Transactional
    public EmployeeResponse deactivate(Long id) {
        Employee employee = getEmployee(id);
        employee.setStatus(EmployeeStatus.INACTIVE);
        return toResponse(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public List<EmployeeCostHistoryResponse> findCostHistory(Long employeeId) {
        getEmployee(employeeId);
        return findCostHistoryEntities(employeeId).stream().map(this::toCostHistoryResponse).toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal getCostAtDate(Long employeeId, LocalDate date) {
        getEmployee(employeeId);
        return costHistoryRepository
                .findTopByEmployeeIdAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(employeeId, date)
                .map(EmployeeCostHistory::getHourlyCost)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No hourly cost is effective for this employee on the pointage date"));
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    private List<EmployeeCostHistory> findCostHistoryEntities(Long employeeId) {
        return costHistoryRepository.findByEmployeeIdOrderByEffectiveDateDescCreatedAtDesc(employeeId);
    }

    private void fillEmployee(Employee employee, EmployeeRequest request) {
        employee.setMatricule(request.matricule().trim());
        employee.setFullName(request.fullName().trim());
        employee.setQualification(request.qualification().trim());
        employee.setContractType(request.contractType());
        employee.setHourlyCost(request.hourlyCost());
        employee.setStatus(request.status());
    }

    private EmployeeCostHistory newCostHistory(Employee employee, BigDecimal hourlyCost, LocalDate effectiveDate) {
        EmployeeCostHistory history = new EmployeeCostHistory();
        history.setEmployee(employee);
        history.setHourlyCost(hourlyCost);
        history.setEffectiveDate(effectiveDate);
        return history;
    }

    private EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getMatricule(),
                employee.getFullName(),
                employee.getQualification(),
                employee.getContractType(),
                employee.getHourlyCost(),
                employee.getStatus(),
                employee.getCreatedAt(),
                employee.getUpdatedAt());
    }

    private EmployeeDetailResponse toDetailResponse(Employee employee, List<EmployeeCostHistory> history) {
        return new EmployeeDetailResponse(
                employee.getId(),
                employee.getMatricule(),
                employee.getFullName(),
                employee.getQualification(),
                employee.getContractType(),
                employee.getHourlyCost(),
                employee.getStatus(),
                employee.getCreatedAt(),
                employee.getUpdatedAt(),
                history.stream().map(this::toCostHistoryResponse).toList());
    }

    private EmployeeCostHistoryResponse toCostHistoryResponse(EmployeeCostHistory history) {
        return new EmployeeCostHistoryResponse(
                history.getId(),
                history.getEmployee().getId(),
                history.getHourlyCost(),
                history.getEffectiveDate(),
                history.getCreatedAt());
    }
}
