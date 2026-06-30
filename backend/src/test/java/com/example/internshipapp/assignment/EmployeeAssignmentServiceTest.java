package com.example.internshipapp.assignment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.example.internshipapp.assignment.dto.EmployeeAssignmentRequest;
import com.example.internshipapp.common.enums.AssignmentStatus;
import com.example.internshipapp.common.enums.EmployeeStatus;
import com.example.internshipapp.common.enums.ProjectStatus;
import com.example.internshipapp.employee.Employee;
import com.example.internshipapp.employee.EmployeeRepository;
import com.example.internshipapp.employee.EmployeeService;
import com.example.internshipapp.project.Project;
import com.example.internshipapp.project.ProjectService;

class EmployeeAssignmentServiceTest {

    @Test
    void usesHourlyCostApplicableOnAssignmentDate() {
        EmployeeAssignmentRepository assignmentRepository = mock(EmployeeAssignmentRepository.class);
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        ProjectService projectService = mock(ProjectService.class);
        EmployeeService employeeService = mock(EmployeeService.class);
        EmployeeAssignmentService service = new EmployeeAssignmentService(
                assignmentRepository, employeeRepository, projectService, employeeService);

        LocalDate date = LocalDate.of(2026, 7, 15);
        Project project = mock(Project.class);
        when(project.getId()).thenReturn(1L);
        when(project.getArchived()).thenReturn(false);
        when(project.getStatus()).thenReturn(ProjectStatus.IN_PROGRESS);

        Employee employee = mock(Employee.class);
        when(employee.getId()).thenReturn(2L);
        when(employee.getFullName()).thenReturn("Salarie test");
        when(employee.getStatus()).thenReturn(EmployeeStatus.ACTIVE);

        when(projectService.getProject(1L)).thenReturn(project);
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(employeeService.getCostAtDate(2L, date)).thenReturn(new BigDecimal("150.00"));
        when(assignmentRepository.save(any(EmployeeAssignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeAssignmentRequest request = new EmployeeAssignmentRequest(
                2L, date, new BigDecimal("8.00"));

        var response = service.create(1L, request);

        assertThat(response.hourlyCostSnapshot()).isEqualByComparingTo("150.00");
        assertThat(response.totalCost()).isEqualByComparingTo("1200.00");
    }

    @Test
    void rejectsEmployeeAlreadyPointedOnAnotherProjectTheSameDay() {
        EmployeeAssignmentRepository assignmentRepository = mock(EmployeeAssignmentRepository.class);
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        ProjectService projectService = mock(ProjectService.class);
        EmployeeAssignmentService service = new EmployeeAssignmentService(
                assignmentRepository,
                employeeRepository,
                projectService,
                mock(EmployeeService.class));

        LocalDate date = LocalDate.of(2026, 7, 15);
        Project project = mock(Project.class);
        when(project.getId()).thenReturn(1L);
        when(project.getArchived()).thenReturn(false);
        when(project.getStatus()).thenReturn(ProjectStatus.IN_PROGRESS);

        Employee employee = mock(Employee.class);
        when(employee.getId()).thenReturn(2L);
        when(employee.getStatus()).thenReturn(EmployeeStatus.ACTIVE);
        when(employee.getHourlyCost()).thenReturn(new BigDecimal("120.00"));

        when(projectService.getProject(1L)).thenReturn(project);
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(assignmentRepository.existsByEmployeeIdAndAssignmentDateAndStatusNot(
                2L, date, AssignmentStatus.CANCELLED)).thenReturn(true);

        EmployeeAssignmentRequest request = new EmployeeAssignmentRequest(
                2L, date, new BigDecimal("8.00"));

        assertThatThrownBy(() -> service.create(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already assigned to another project");
    }
}
