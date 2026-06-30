package com.example.internshipapp.expense;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseDocumentRepository extends JpaRepository<ExpenseDocument, Long> {

    List<ExpenseDocument> findByExpenseIdOrderByCreatedAtDesc(Long expenseId);
}
