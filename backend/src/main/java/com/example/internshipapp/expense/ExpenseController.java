package com.example.internshipapp.expense;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.internshipapp.expense.dto.ExpenseRequest;
import com.example.internshipapp.expense.dto.ExpenseResponse;

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

    @GetMapping("/expenses/{id}")
    public ExpenseResponse findById(@PathVariable Long id) {
        return expenseService.findById(id);
    }

    @PutMapping("/expenses/{id}")
    public ExpenseResponse update(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        return expenseService.update(id, request);
    }

    @PatchMapping("/expenses/{id}/cancel")
    public ExpenseResponse cancel(@PathVariable Long id) {
        return expenseService.cancel(id);
    }
}
