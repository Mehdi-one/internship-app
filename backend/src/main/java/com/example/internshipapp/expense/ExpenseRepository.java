package com.example.internshipapp.expense;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByProjectIdOrderByExpenseDateDesc(Long projectId);

    @Query("""
            select coalesce(sum(expense.amountHT), 0)
            from Expense expense
            where expense.project.id = :projectId
            and expense.status <> com.example.internshipapp.common.enums.ExpenseStatus.CANCELLED
            """)
    BigDecimal sumNonCancelledExpensesByProjectId(@Param("projectId") Long projectId);
}
