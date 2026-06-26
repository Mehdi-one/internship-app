package com.example.internshipapp.expense;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internshipapp.common.enums.ExpenseCategory;
import com.example.internshipapp.common.enums.ExpenseStatus;
import com.example.internshipapp.common.exception.ResourceNotFoundException;
import com.example.internshipapp.expense.dto.ExpenseRequest;
import com.example.internshipapp.expense.dto.ExpenseResponse;
import com.example.internshipapp.project.Project;
import com.example.internshipapp.project.ProjectLot;
import com.example.internshipapp.project.ProjectLotRepository;
import com.example.internshipapp.project.ProjectService;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ProjectLotRepository projectLotRepository;
    private final ProjectService projectService;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            ProjectLotRepository projectLotRepository,
            ProjectService projectService) {
        this.expenseRepository = expenseRepository;
        this.projectLotRepository = projectLotRepository;
        this.projectService = projectService;
    }

    @Transactional
    public ExpenseResponse create(Long projectId, ExpenseRequest request) {
        Project project = projectService.getProject(projectId);

        Expense expense = new Expense();
        expense.setProject(project);
        fillExpense(expense, project, request);

        return toResponse(expenseRepository.save(expense));
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> findByProject(Long projectId) {
        projectService.getProject(projectId);

        return expenseRepository.findByProjectIdOrderByExpenseDateDesc(projectId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExpenseResponse findById(Long id) {
        return toResponse(getExpense(id));
    }

    @Transactional
    public ExpenseResponse update(Long id, ExpenseRequest request) {
        Expense expense = getExpense(id);
        fillExpense(expense, expense.getProject(), request);
        return toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseResponse cancel(Long id) {
        Expense expense = getExpense(id);
        expense.setStatus(ExpenseStatus.CANCELLED);
        return toResponse(expenseRepository.save(expense));
    }

    private Expense getExpense(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
    }

    private void fillExpense(Expense expense, Project project, ExpenseRequest request) {
        expense.setProjectLot(resolveProjectLot(project, request.projectLotId()));
        expense.setCategory(request.category() == null ? ExpenseCategory.OTHER : request.category());
        expense.setDescription(request.description());
        expense.setAmountHT(request.amountHT());
        expense.setTvaRate(request.tvaRate());
        expense.setSupplierName(request.supplierName());
        expense.setInvoiceNumber(request.invoiceNumber());
        expense.setExpenseDate(request.expenseDate());
        expense.setStatus(request.status() == null ? ExpenseStatus.COMMITTED : request.status());
    }

    private ProjectLot resolveProjectLot(Project project, Long projectLotId) {
        if (projectLotId == null) {
            return null;
        }

        ProjectLot projectLot = projectLotRepository.findById(projectLotId)
                .orElseThrow(() -> new ResourceNotFoundException("Project lot not found"));

        if (!projectLot.getProject().getId().equals(project.getId())) {
            throw new IllegalArgumentException("Project lot does not belong to this project");
        }

        if (projectLot.isArchived()) {
            throw new IllegalArgumentException("Cannot attach an expense to an archived lot/poste");
        }

        return projectLot;
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getProject().getId(),
                expense.getProjectLot() == null ? null : expense.getProjectLot().getId(),
                expense.getProjectLot() == null ? null : expense.getProjectLot().getDesignation(),
                expense.getCategory(),
                expense.getDescription(),
                expense.getAmountHT(),
                expense.getTvaRate(),
                expense.getSupplierName(),
                expense.getInvoiceNumber(),
                expense.getExpenseDate(),
                expense.getStatus(),
                expense.getCreatedAt(),
                expense.getUpdatedAt());
    }
}
