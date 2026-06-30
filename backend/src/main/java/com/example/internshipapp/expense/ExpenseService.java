package com.example.internshipapp.expense;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internshipapp.common.enums.ExpenseCategory;
import com.example.internshipapp.common.enums.ExpenseStatus;
import com.example.internshipapp.common.enums.ExpenseType;
import com.example.internshipapp.common.exception.ResourceNotFoundException;
import com.example.internshipapp.expense.dto.ExpenseRequest;
import com.example.internshipapp.expense.dto.ExpenseResponse;
import com.example.internshipapp.expense.dto.ExpenseDocumentResponse;
import com.example.internshipapp.project.Project;
import com.example.internshipapp.project.ProjectLot;
import com.example.internshipapp.project.ProjectLotRepository;
import com.example.internshipapp.project.ProjectService;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ProjectLotRepository projectLotRepository;
    private final ProjectService projectService;
    private final ExpenseDocumentRepository documentRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            ProjectLotRepository projectLotRepository,
            ProjectService projectService,
            ExpenseDocumentRepository documentRepository) {
        this.expenseRepository = expenseRepository;
        this.projectLotRepository = projectLotRepository;
        this.projectService = projectService;
        this.documentRepository = documentRepository;
    }

    @Transactional
    public ExpenseResponse create(Long projectId, ExpenseRequest request) {
        Project project = projectService.getProject(projectId);

        Expense expense = new Expense();
        expense.setProject(project);
        fillExpense(expense, project, request);
        expense.setStatus(ExpenseStatus.COMMITTED);

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
    public List<ExpenseResponse> findAll(String search, ExpenseCategory category, ExpenseStatus status) {
        String normalizedSearch = search == null ? "" : search.trim();
        return expenseRepository.search(normalizedSearch, category, status).stream()
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
        requireStatus(expense, ExpenseStatus.COMMITTED, "Only a committed expense can be modified");
        fillExpense(expense, expense.getProject(), request);
        return toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseResponse markAsInvoiced(Long id) {
        Expense expense = getExpense(id);
        requireStatus(expense, ExpenseStatus.COMMITTED, "Only a committed expense can be invoiced");
        if (expense.getInvoiceNumber() == null || expense.getInvoiceNumber().isBlank()) {
            throw new IllegalArgumentException("An invoice number is required before invoicing an expense");
        }
        expense.setStatus(ExpenseStatus.INVOICED);
        return toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseResponse markAsPaid(Long id) {
        Expense expense = getExpense(id);
        if (expense.getStatus() != ExpenseStatus.COMMITTED && expense.getStatus() != ExpenseStatus.INVOICED) {
            throw new IllegalArgumentException("Only a committed or invoiced expense can be paid");
        }
        expense.setStatus(ExpenseStatus.PAID);
        return toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseResponse cancel(Long id) {
        Expense expense = getExpense(id);
        if (expense.getStatus() != ExpenseStatus.COMMITTED && expense.getStatus() != ExpenseStatus.INVOICED) {
            throw new IllegalArgumentException("Only a committed or invoiced expense can be cancelled");
        }
        expense.setStatus(ExpenseStatus.CANCELLED);
        return toResponse(expenseRepository.save(expense));
    }

    private Expense getExpense(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
    }

    private void fillExpense(Expense expense, Project project, ExpenseRequest request) {
        validateExpenseDate(project, request.expenseDate());
        expense.setProjectLot(resolveProjectLot(project, request.projectLotId()));
        validateExpenseType(request.category(), request.expenseType());
        expense.setCategory(request.category());
        expense.setExpenseType(request.expenseType());
        expense.setDescription(normalize(request.description()));
        expense.setAmountHT(request.amountHT());
        expense.setTvaRate(request.tvaRate());
        expense.setSupplierName(normalize(request.supplierName()));
        expense.setInvoiceNumber(normalize(request.invoiceNumber()));
        expense.setExpenseDate(request.expenseDate());
    }

    private void requireStatus(Expense expense, ExpenseStatus expectedStatus, String message) {
        if (expense.getStatus() != expectedStatus) {
            throw new IllegalArgumentException(message);
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
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

    private void validateExpenseDate(Project project, LocalDate expenseDate) {
        if (project.getNotificationOrderDate() != null && expenseDate.isBefore(project.getNotificationOrderDate())) {
            throw new IllegalArgumentException("Expense date cannot be before project start date");
        }

        if (project.getPlannedEndDate() != null && expenseDate.isAfter(project.getPlannedEndDate())) {
            throw new IllegalArgumentException("Expense date cannot be after project end date");
        }
    }

    private void validateExpenseType(ExpenseCategory category, ExpenseType expenseType) {
        boolean valid = switch (category) {
            case MATERIALS -> expenseType == ExpenseType.MATERIAL_PURCHASE
                    || expenseType == ExpenseType.MATERIAL_TRANSPORT;
            case EQUIPMENT -> expenseType == ExpenseType.EQUIPMENT_FUEL
                    || expenseType == ExpenseType.EQUIPMENT_MAINTENANCE
                    || expenseType == ExpenseType.EQUIPMENT_TRANSPORT
                    || expenseType == ExpenseType.EQUIPMENT_EXTERNAL_RENTAL;
            case EMPLOYEES -> expenseType == ExpenseType.EMPLOYEE_DAILY_EXPENSES
                    || expenseType == ExpenseType.EMPLOYEE_SALARY_ADVANCE;
            case COMPANY_STAFF -> expenseType == ExpenseType.COMPANY_STAFF_DAILY_EXPENSES;
            case SUBCONTRACTING -> expenseType == ExpenseType.SUBCONTRACTING_SERVICE;
            case SITE_FEES -> expenseType == ExpenseType.SITE_EXPENSE;
            case GENERAL_FEES -> expenseType == ExpenseType.GENERAL_EXPENSE;
            case OTHER -> expenseType == ExpenseType.OTHER_EXPENSE;
            case EXTERNAL_RENTAL -> expenseType == ExpenseType.EQUIPMENT_EXTERNAL_RENTAL;
        };

        if (!valid) {
            throw new IllegalArgumentException("Expense type does not match its category");
        }
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getProject().getId(),
                expense.getProject().getReference(),
                expense.getProject().getTitle(),
                expense.getProjectLot() == null ? null : expense.getProjectLot().getId(),
                expense.getProjectLot() == null ? null : expense.getProjectLot().getDesignation(),
                expense.getCategory(),
                expense.getExpenseType(),
                expense.getDescription(),
                expense.getAmountHT(),
                expense.getTvaRate(),
                expense.getSupplierName(),
                expense.getInvoiceNumber(),
                expense.getExpenseDate(),
                expense.getStatus(),
                documentRepository.findByExpenseIdOrderByCreatedAtDesc(expense.getId()).stream()
                        .map(document -> new ExpenseDocumentResponse(
                                document.getId(),
                                expense.getId(),
                                document.getDocumentType(),
                                document.getOriginalFileName(),
                                document.getContentType(),
                                document.getFileSize(),
                                document.getCreatedAt()))
                        .toList(),
                expense.getCreatedAt(),
                expense.getUpdatedAt());
    }
}
