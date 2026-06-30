package com.example.internshipapp.project;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.internshipapp.assignment.EmployeeAssignmentRepository;
import com.example.internshipapp.assignment.EquipmentAssignmentRepository;
import com.example.internshipapp.common.enums.ProjectStatus;
import com.example.internshipapp.common.exception.ResourceNotFoundException;
import com.example.internshipapp.expense.ExpenseRepository;
import com.example.internshipapp.project.dto.ProjectRequest;
import com.example.internshipapp.project.dto.ProjectResponse;
import com.example.internshipapp.project.dto.ProjectSummaryResponse;
import com.example.internshipapp.project.dto.FinancialSummaryResponse;

@Service
public class ProjectService {

    private static final Set<BigDecimal> ALLOWED_TVA_RATES = Set.of(
            BigDecimal.ZERO,
            BigDecimal.valueOf(7),
            BigDecimal.TEN,
            BigDecimal.valueOf(14),
            BigDecimal.valueOf(20));

    private final ProjectRepository projectRepository;
    private final ExpenseRepository expenseRepository;
    private final EmployeeAssignmentRepository employeeAssignmentRepository;
    private final EquipmentAssignmentRepository equipmentAssignmentRepository;
    private BigDecimal budgetAlertThreshold = BigDecimal.valueOf(80);

    public ProjectService(
            ProjectRepository projectRepository,
            ExpenseRepository expenseRepository,
            EmployeeAssignmentRepository employeeAssignmentRepository,
            EquipmentAssignmentRepository equipmentAssignmentRepository) {
        this.projectRepository = projectRepository;
        this.expenseRepository = expenseRepository;
        this.employeeAssignmentRepository = employeeAssignmentRepository;
        this.equipmentAssignmentRepository = equipmentAssignmentRepository;
    }

    @Value("${app.alert.budget-threshold:80.0}")
    void setBudgetAlertThreshold(BigDecimal budgetAlertThreshold) {
        this.budgetAlertThreshold = budgetAlertThreshold;
    }

    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        if (projectRepository.existsByReference(request.reference())) {
            throw new IllegalArgumentException("Project reference already exists");
        }

        Project project = new Project();
        fillProject(project, request);

        return toResponse(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll(String search, ProjectStatus status) {
        String normalizedSearch = search == null || search.isBlank() ? null : search.trim();
        List<Project> projects;

        if (normalizedSearch == null && status == null) {
            projects = projectRepository.findAllByOrderByUpdatedAtDesc();
        } else if (normalizedSearch == null) {
            projects = projectRepository.findByStatusOrderByUpdatedAtDesc(status);
        } else if (status == null) {
            projects = projectRepository.search(normalizedSearch);
        } else {
            projects = projectRepository.searchByStatus(normalizedSearch, status);
        }

        return projects.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) {
        return toResponse(getProject(id));
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = getProject(id);

        projectRepository.findByReference(request.reference())
                .filter(existingProject -> !existingProject.getId().equals(id))
                .ifPresent(existingProject -> {
                    throw new IllegalArgumentException("Project reference already exists");
                });

        fillProject(project, request);
        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse close(Long id) {
        Project project = getProject(id);
        project.setStatus(ProjectStatus.CLOSED);
        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse archive(Long id) {
        Project project = getProject(id);
        if (project.getStatus() != ProjectStatus.CLOSED) {
            throw new IllegalArgumentException("Only a closed project can be archived");
        }
        project.setArchived(true);
        return toResponse(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public ProjectSummaryResponse getSummary(Long id) {
        Project project = getProject(id);
        BigDecimal directExpenses = expenseRepository.sumNonCancelledExpensesByProjectId(id);
        BigDecimal laborCost = employeeAssignmentRepository.sumValidatedCostByProjectId(id);
        BigDecimal equipmentCost = equipmentAssignmentRepository.sumValidatedCostByProjectId(id);
        BigDecimal totalExpenses = directExpenses.add(laborCost).add(equipmentCost);
        BigDecimal forecastMargin = project.getAwardedAmountHT().subtract(project.getEstimatedDryCost());
        BigDecimal provisionalGrossMargin = project.getAwardedAmountHT().subtract(totalExpenses);
        BigDecimal remainingBudget = project.getEstimatedDryCost().subtract(totalExpenses);

        return new ProjectSummaryResponse(
                project.getId(),
                project.getReference(),
                project.getTitle(),
                project.getAwardedAmountHT(),
                project.getEstimatedDryCost(),
                directExpenses,
                laborCost,
                equipmentCost,
                totalExpenses,
                forecastMargin,
                percentage(forecastMargin, project.getAwardedAmountHT()),
                provisionalGrossMargin,
                percentage(provisionalGrossMargin, project.getAwardedAmountHT()),
                remainingBudget,
                percentage(totalExpenses, project.getEstimatedDryCost()),
                project.getStatus());
    }

    @Transactional(readOnly = true)
    public FinancialSummaryResponse getFinancialSummary(Long id) {
        ProjectSummaryResponse summary = getSummary(id);
        boolean canViewMargin = currentUserHasRole("DIRIGEANT") || currentUserHasRole("ADMIN");
        BigDecimal consumption = summary.budgetConsumptionRate();
        boolean budgetCritical = consumption.compareTo(BigDecimal.valueOf(100)) >= 0;
        boolean budgetAlert = consumption.compareTo(budgetAlertThreshold) >= 0;

        return new FinancialSummaryResponse(
                summary.projectId(),
                summary.estimatedDryCost(),
                summary.totalExpenses(),
                summary.laborCost(),
                summary.equipmentCost(),
                summary.directExpenses(),
                consumption,
                budgetAlertThreshold,
                budgetAlert,
                budgetCritical,
                canViewMargin ? summary.awardedAmountHT() : null,
                canViewMargin ? summary.provisionalGrossMargin() : null,
                canViewMargin ? summary.provisionalGrossMarginRate() : null);
    }

    public Project getProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    private void fillProject(Project project, ProjectRequest request) {
        project.setReference(request.reference());
        project.setTitle(request.title());
        validateTvaRate(request.tvaRate());
        project.setContractingAuthority(request.contractingAuthority());
        project.setProjectType(request.projectType());
        project.setAwardedAmountHT(request.awardedAmountHT());
        project.setTvaRate(request.tvaRate());
        project.setEstimatedDryCost(request.estimatedDryCost());
        project.setNotificationOrderDate(request.notificationOrderDate());
        project.setExecutionDelayDays(request.executionDelayDays());
        project.setPlannedEndDate(calculatePlannedEndDate(
                request.notificationOrderDate(), request.executionDelayDays()));
        project.setResponsibleUserReference(request.responsibleUserReference());
        project.setStatus(request.status() == null ? ProjectStatus.PROSPECT : request.status());
    }

    private LocalDate calculatePlannedEndDate(LocalDate notificationOrderDate, Integer executionDelayDays) {
        if (notificationOrderDate == null || executionDelayDays == null) {
            return null;
        }
        return notificationOrderDate.plusDays(executionDelayDays - 1L);
    }

    private void validateTvaRate(BigDecimal tvaRate) {
        boolean allowed = ALLOWED_TVA_RATES.stream()
                .anyMatch(rate -> rate.compareTo(tvaRate) == 0);
        if (!allowed) {
            throw new IllegalArgumentException("TVA rate must be one of: 0, 7, 10, 14, 20");
        }
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getReference(),
                project.getTitle(),
                project.getContractingAuthority(),
                project.getProjectType(),
                project.getAwardedAmountHT(),
                project.getTvaRate(),
                project.getEstimatedDryCost(),
                project.getNotificationOrderDate(),
                project.getExecutionDelayDays(),
                project.getPlannedEndDate(),
                project.getResponsibleUserReference(),
                Boolean.TRUE.equals(project.getArchived()),
                project.getStatus(),
                project.getCreatedAt(),
                project.getUpdatedAt());
    }

    private BigDecimal percentage(BigDecimal value, BigDecimal total) {
        if (total == null || BigDecimal.ZERO.compareTo(total) == 0) {
            return BigDecimal.ZERO;
        }

        return value.multiply(BigDecimal.valueOf(100))
                .divide(total, 2, RoundingMode.HALF_UP);
    }

    private boolean currentUserHasRole(String role) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }
}
