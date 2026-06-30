package com.example.internshipapp.expense;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.internshipapp.expense.dto.ExpenseRequest;
import com.example.internshipapp.expense.dto.ExpenseResponse;
import com.example.internshipapp.common.enums.ExpenseCategory;
import com.example.internshipapp.common.enums.ExpenseStatus;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/projects/{projectId}/expenses")
    public ExpenseResponse create(@PathVariable Long projectId, @Valid @RequestBody ExpenseRequest request) {
        return expenseService.create(projectId, request);
    }

    @GetMapping("/projects/{projectId}/expenses")
    public List<ExpenseResponse> findByProject(@PathVariable Long projectId) {
        return expenseService.findByProject(projectId);
    }

    @GetMapping("/expenses")
    public List<ExpenseResponse> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ExpenseCategory category,
            @RequestParam(required = false) ExpenseStatus status) {
        return expenseService.findAll(search, category, status);
    }

    @GetMapping("/expenses/{id}")
    public ExpenseResponse findById(@PathVariable Long id) {
        return expenseService.findById(id);
    }

    @PutMapping("/expenses/{id}")
    public ExpenseResponse update(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        return expenseService.update(id, request);
    }

    @PatchMapping("/expenses/{id}/invoice")
    public ExpenseResponse markAsInvoiced(@PathVariable Long id) {
        return expenseService.markAsInvoiced(id);
    }

    @PatchMapping("/expenses/{id}/pay")
    public ExpenseResponse markAsPaid(@PathVariable Long id) {
        return expenseService.markAsPaid(id);
    }

    @PatchMapping("/expenses/{id}/cancel")
    public ExpenseResponse cancel(@PathVariable Long id) {
        return expenseService.cancel(id);
    }
}
