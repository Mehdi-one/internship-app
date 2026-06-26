package com.example.internshipapp.project;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internshipapp.common.enums.ProjectStatus;
import com.example.internshipapp.common.exception.ResourceNotFoundException;
import com.example.internshipapp.expense.ExpenseRepository;
import com.example.internshipapp.project.dto.ProjectRequest;
import com.example.internshipapp.project.dto.ProjectResponse;
import com.example.internshipapp.project.dto.ProjectSummaryResponse;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ExpenseRepository expenseRepository;

    public ProjectService(ProjectRepository projectRepository, ExpenseRepository expenseRepository) {
        this.projectRepository = projectRepository;
        this.expenseRepository = expenseRepository;
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
        project.setArchived(true);
        return toResponse(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public ProjectSummaryResponse getSummary(Long id) {
        Project project = getProject(id);
        BigDecimal totalExpenses = expenseRepository.sumNonCancelledExpensesByProjectId(id);
        BigDecimal margin = project.getAmountHT().subtract(totalExpenses);

        return new ProjectSummaryResponse(
                project.getId(),
                project.getReference(),
                project.getTitle(),
                project.getAmountHT(),
                project.getEstimatedBudget(),
                totalExpenses,
                margin,
                percentage(margin, project.getAmountHT()),
                percentage(totalExpenses, project.getEstimatedBudget()),
                project.getStatus());
    }

    public Project getProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    private void fillProject(Project project, ProjectRequest request) {
        project.setReference(request.reference());
        project.setTitle(request.title());
        project.setClientName(request.clientName());
        project.setProjectType(request.projectType());
        project.setAmountHT(request.amountHT());
        project.setTvaRate(request.tvaRate());
        project.setEstimatedBudget(request.estimatedBudget());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
        project.setExecutionDelayDays(request.executionDelayDays());
        project.setResponsibleName(request.responsibleName());
        project.setStatus(request.status() == null ? ProjectStatus.IN_PROGRESS : request.status());
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getReference(),
                project.getTitle(),
                project.getClientName(),
                project.getProjectType(),
                project.getAmountHT(),
                project.getTvaRate(),
                project.getEstimatedBudget(),
                project.getStartDate(),
                project.getEndDate(),
                project.getExecutionDelayDays(),
                project.getResponsibleName(),
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
}
