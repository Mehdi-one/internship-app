package com.example.internshipapp.expense;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.internshipapp.common.enums.ExpenseCategory;
import com.example.internshipapp.common.enums.ExpenseStatus;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByProjectIdOrderByExpenseDateDesc(Long projectId);

    @Query("""
            select expense from Expense expense
            join expense.project project
            where (:category is null or expense.category = :category)
              and (:status is null or expense.status = :status)
              and (:search = ''
                   or lower(project.reference) like lower(concat('%', :search, '%'))
                   or lower(project.title) like lower(concat('%', :search, '%'))
                   or lower(coalesce(expense.description, '')) like lower(concat('%', :search, '%'))
                   or lower(coalesce(expense.supplierName, '')) like lower(concat('%', :search, '%'))
                   or lower(coalesce(expense.invoiceNumber, '')) like lower(concat('%', :search, '%')))
            order by expense.expenseDate desc, expense.id desc
            """)
    List<Expense> search(
            @Param("search") String search,
            @Param("category") ExpenseCategory category,
            @Param("status") ExpenseStatus status);

    @Query("""
            select coalesce(sum(expense.amountHT), 0)
            from Expense expense
            where expense.project.id = :projectId
            and expense.status <> com.example.internshipapp.common.enums.ExpenseStatus.CANCELLED
            """)
    BigDecimal sumNonCancelledExpensesByProjectId(@Param("projectId") Long projectId);
}
