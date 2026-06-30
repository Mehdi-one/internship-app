package com.example.internshipapp.assignment;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.internshipapp.common.enums.AssignmentStatus;

public interface EmployeeAssignmentRepository extends JpaRepository<EmployeeAssignment, Long> {

    List<EmployeeAssignment> findByProjectIdOrderByAssignmentDateDesc(Long projectId);

    boolean existsByEmployeeIdAndAssignmentDateAndStatusNot(
            Long employeeId, LocalDate assignmentDate, AssignmentStatus status);

    boolean existsByEmployeeIdAndAssignmentDateAndStatusNotAndIdNot(
            Long employeeId, LocalDate assignmentDate, AssignmentStatus status, Long id);

    @Query("""
            select coalesce(sum(assignment.totalCost), 0)
            from EmployeeAssignment assignment
            where assignment.project.id = :projectId
            and assignment.status = com.example.internshipapp.common.enums.AssignmentStatus.VALIDATED
            """)
    BigDecimal sumValidatedCostByProjectId(@Param("projectId") Long projectId);
}
