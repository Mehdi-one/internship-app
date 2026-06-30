package com.example.internshipapp.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.internshipapp.assignment.EmployeeAssignmentRepository;
import com.example.internshipapp.assignment.EquipmentAssignmentRepository;
import com.example.internshipapp.common.enums.ProjectStatus;
import com.example.internshipapp.common.enums.ProjectType;
import com.example.internshipapp.expense.ExpenseRepository;
import com.example.internshipapp.project.dto.ProjectRequest;
import com.example.internshipapp.project.dto.ProjectResponse;
import com.example.internshipapp.project.dto.FinancialSummaryResponse;

class ProjectServiceTest {

    private ProjectRepository projectRepository;
    private ExpenseRepository expenseRepository;
    private EmployeeAssignmentRepository employeeAssignmentRepository;
    private EquipmentAssignmentRepository equipmentAssignmentRepository;
    private ProjectService service;

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        expenseRepository = mock(ExpenseRepository.class);
        employeeAssignmentRepository = mock(EmployeeAssignmentRepository.class);
        equipmentAssignmentRepository = mock(EquipmentAssignmentRepository.class);
        service = new ProjectService(
                projectRepository,
                expenseRepository,
                employeeAssignmentRepository,
                equipmentAssignmentRepository);
        service.setBudgetAlertThreshold(BigDecimal.valueOf(80));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void rejectsArchiveBeforeProjectClosure() {
        Project project = projectWithStatus(ProjectStatus.IN_PROGRESS);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> service.archive(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only a closed project can be archived");

        assertThat(project.getArchived()).isFalse();
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void archivesClosedProject() {
        Project project = projectWithStatus(ProjectStatus.CLOSED);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        ProjectResponse response = service.archive(1L);

        assertThat(response.archived()).isTrue();
        assertThat(project.getArchived()).isTrue();
        verify(projectRepository).save(project);
    }

    @Test
    void calculatesPlannedEndDateFromNotificationOrderAndExecutionDelay() {
        ProjectRequest request = projectRequest(BigDecimal.valueOf(20));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project project = invocation.getArgument(0);
            project.onCreate();
            return project;
        });

        ProjectResponse response = service.create(request);

        assertThat(response.notificationOrderDate()).isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(response.executionDelayDays()).isEqualTo(30);
        assertThat(response.plannedEndDate()).isEqualTo(LocalDate.of(2026, 7, 30));
        assertThat(response.status()).isEqualTo(ProjectStatus.PROSPECT);
    }

    @Test
    void rejectsTvaRateOutsideCdcList() {
        ProjectRequest request = projectRequest(BigDecimal.valueOf(5));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("TVA rate must be one of: 0, 7, 10, 14, 20");

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void exposesMarginForDirigeant() {
        configureFinancialSummary("400.00");
        authenticateAs("DIRIGEANT");

        FinancialSummaryResponse response = service.getFinancialSummary(1L);

        assertThat(response.margeBrute()).isEqualByComparingTo("600.00");
        assertThat(response.tauxMarge()).isEqualByComparingTo("60.00");
    }

    @Test
    void hidesMarginForConducteur() {
        configureFinancialSummary("400.00");
        authenticateAs("CONDUCTEUR");

        FinancialSummaryResponse response = service.getFinancialSummary(1L);

        assertThat(response.montantHT()).isNull();
        assertThat(response.margeBrute()).isNull();
        assertThat(response.tauxMarge()).isNull();
    }

    @Test
    void raisesBudgetAlertAtConfiguredThreshold() {
        configureFinancialSummary("800.00");
        authenticateAs("DIRIGEANT");

        FinancialSummaryResponse response = service.getFinancialSummary(1L);

        assertThat(response.consommationBudget()).isEqualByComparingTo("80.00");
        assertThat(response.budgetAlert()).isTrue();
        assertThat(response.budgetCritique()).isFalse();
    }

    @Test
    void marksBudgetCriticalAtOneHundredPercent() {
        configureFinancialSummary("1000.00");
        authenticateAs("ADMIN");

        FinancialSummaryResponse response = service.getFinancialSummary(1L);

        assertThat(response.budgetCritique()).isTrue();
        assertThat(response.budgetAlert()).isTrue();
    }

    private ProjectRequest projectRequest(BigDecimal tvaRate) {
        return new ProjectRequest(
                "M-CDC-001",
                "Construction siege",
                "Commune",
                ProjectType.WORKS,
                BigDecimal.valueOf(1_000_000),
                tvaRate,
                BigDecimal.valueOf(700_000),
                LocalDate.of(2026, 7, 1),
                30,
                "conducteur-1",
                ProjectStatus.PROSPECT);
    }

    private Project projectWithStatus(ProjectStatus status) {
        Project project = new Project();
        project.setStatus(status);
        project.setArchived(false);
        return project;
    }

    private void configureFinancialSummary(String directExpenses) {
        Project project = projectWithStatus(ProjectStatus.IN_PROGRESS);
        project.setAwardedAmountHT(new BigDecimal("1000.00"));
        project.setEstimatedDryCost(new BigDecimal("1000.00"));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(expenseRepository.sumNonCancelledExpensesByProjectId(1L))
                .thenReturn(new BigDecimal(directExpenses));
        when(employeeAssignmentRepository.sumValidatedCostByProjectId(1L)).thenReturn(BigDecimal.ZERO);
        when(equipmentAssignmentRepository.sumValidatedCostByProjectId(1L)).thenReturn(BigDecimal.ZERO);
    }

    private void authenticateAs(String role) {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("test", null, "ROLE_" + role));
    }
}
