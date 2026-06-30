package com.example.internshipapp.assignment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internshipapp.assignment.dto.EmployeeAssignmentRequest;
import com.example.internshipapp.assignment.dto.EmployeeAssignmentResponse;
import com.example.internshipapp.common.enums.AssignmentStatus;
import com.example.internshipapp.common.enums.EmployeeStatus;
import com.example.internshipapp.common.exception.ResourceNotFoundException;
import com.example.internshipapp.employee.Employee;
import com.example.internshipapp.employee.EmployeeRepository;
import com.example.internshipapp.employee.EmployeeService;
import com.example.internshipapp.project.Project;
import com.example.internshipapp.project.ProjectService;
import com.example.internshipapp.common.enums.ProjectStatus;

@Service
public class EmployeeAssignmentService {

    private final EmployeeAssignmentRepository employeeAssignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectService projectService;
    private final EmployeeService employeeService;

    public EmployeeAssignmentService(
            EmployeeAssignmentRepository employeeAssignmentRepository,
            EmployeeRepository employeeRepository,
            ProjectService projectService,
            EmployeeService employeeService) {
        this.employeeAssignmentRepository = employeeAssignmentRepository;
        this.employeeRepository = employeeRepository;
        this.projectService = projectService;
        this.employeeService = employeeService;
    }

    @Transactional
    public EmployeeAssignmentResponse create(Long projectId, EmployeeAssignmentRequest request) {
        Project project = projectService.getProject(projectId);
        Employee employee = getEmployee(request.employeeId());
        validateProject(project);
        validateEmployee(employee);
        validateAssignmentDate(project, request.assignmentDate());
        validateDuplicate(employee.getId(), request.assignmentDate(), null);

        EmployeeAssignment assignment = new EmployeeAssignment();
        assignment.setProject(project);
        assignment.setEmployee(employee);
        assignment.setAssignmentDate(request.assignmentDate());
        assignment.setHours(request.hours());
        BigDecimal hourlyCost = employeeService.getCostAtDate(employee.getId(), request.assignmentDate());
        assignment.setHourlyCostSnapshot(hourlyCost);
        assignment.setTotalCost(request.hours().multiply(hourlyCost));
        assignment.setStatus(AssignmentStatus.DRAFT);

        return toResponse(employeeAssignmentRepository.save(assignment));
    }

    @Transactional
    public EmployeeAssignmentResponse update(Long id, EmployeeAssignmentRequest request) {
        EmployeeAssignment assignment = getAssignment(id);
        requireDraft(assignment);

        Project project = assignment.getProject();
        Employee employee = getEmployee(request.employeeId());
        validateProject(project);
        validateEmployee(employee);
        validateAssignmentDate(project, request.assignmentDate());
        validateDuplicate(employee.getId(), request.assignmentDate(), id);

        assignment.setEmployee(employee);
        assignment.setAssignmentDate(request.assignmentDate());
        assignment.setHours(request.hours());
        BigDecimal hourlyCost = employeeService.getCostAtDate(employee.getId(), request.assignmentDate());
        assignment.setHourlyCostSnapshot(hourlyCost);
        assignment.setTotalCost(request.hours().multiply(hourlyCost));
        return toResponse(employeeAssignmentRepository.save(assignment));
    }

    @Transactional(readOnly = true)
    public List<EmployeeAssignmentResponse> findByProject(Long projectId) {
        projectService.getProject(projectId);

        return employeeAssignmentRepository.findByProjectIdOrderByAssignmentDateDesc(projectId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public EmployeeAssignmentResponse validate(Long id) {
        EmployeeAssignment assignment = getAssignment(id);
        requireDraft(assignment);
        validateProject(assignment.getProject());
        validateEmployee(assignment.getEmployee());
        assignment.setStatus(AssignmentStatus.VALIDATED);
        return toResponse(employeeAssignmentRepository.save(assignment));
    }

    @Transactional
    public EmployeeAssignmentResponse cancel(Long id) {
        EmployeeAssignment assignment = getAssignment(id);
        if (assignment.getStatus() == AssignmentStatus.CANCELLED) {
            throw new IllegalArgumentException("A cancelled assignment cannot be cancelled again");
        }
        assignment.setStatus(AssignmentStatus.CANCELLED);
        return toResponse(employeeAssignmentRepository.save(assignment));
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    private EmployeeAssignment getAssignment(Long id) {
        return employeeAssignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee assignment not found"));
    }

    private void validateProject(Project project) {
        if (Boolean.TRUE.equals(project.getArchived()) || project.getStatus() != ProjectStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Assignments are only allowed on an active project in progress");
        }
    }

    private void validateEmployee(Employee employee) {
        if (employee.getStatus() == EmployeeStatus.INACTIVE) {
            throw new IllegalArgumentException("Cannot assign an inactive employee");
        }
    }

    private void requireDraft(EmployeeAssignment assignment) {
        if (assignment.getStatus() != AssignmentStatus.DRAFT) {
            throw new IllegalArgumentException("Only a draft assignment can be modified or validated");
        }
    }

    private void validateDuplicate(Long employeeId, LocalDate date, Long currentId) {
        boolean exists = currentId == null
                ? employeeAssignmentRepository.existsByEmployeeIdAndAssignmentDateAndStatusNot(
                        employeeId, date, AssignmentStatus.CANCELLED)
                : employeeAssignmentRepository.existsByEmployeeIdAndAssignmentDateAndStatusNotAndIdNot(
                        employeeId, date, AssignmentStatus.CANCELLED, currentId);
        if (exists) {
            throw new IllegalArgumentException("This employee is already assigned to another project on this date");
        }
    }

    private void validateAssignmentDate(Project project, LocalDate assignmentDate) {
        if (project.getNotificationOrderDate() != null && assignmentDate.isBefore(project.getNotificationOrderDate())) {
            throw new IllegalArgumentException("Assignment date cannot be before project start date");
        }

        if (project.getPlannedEndDate() != null && assignmentDate.isAfter(project.getPlannedEndDate())) {
            throw new IllegalArgumentException("Assignment date cannot be after project end date");
        }
    }

    private EmployeeAssignmentResponse toResponse(EmployeeAssignment assignment) {
        return new EmployeeAssignmentResponse(
                assignment.getId(),
                assignment.getProject().getId(),
                assignment.getEmployee().getId(),
                assignment.getEmployee().getFullName(),
                assignment.getAssignmentDate(),
                assignment.getHours(),
                assignment.getHourlyCostSnapshot(),
                assignment.getTotalCost(),
                assignment.getStatus(),
                assignment.getCreatedAt(),
                assignment.getUpdatedAt());
    }
}
