package com.example.internshipapp.assignment;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.internshipapp.common.enums.AssignmentStatus;

public interface EquipmentAssignmentRepository extends JpaRepository<EquipmentAssignment, Long> {

    List<EquipmentAssignment> findByProjectIdOrderByAssignmentDateDesc(Long projectId);

    boolean existsByEquipmentIdAndAssignmentDateAndStatusNot(
            Long equipmentId, LocalDate assignmentDate, AssignmentStatus status);

    boolean existsByEquipmentIdAndAssignmentDateAndStatusNotAndIdNot(
            Long equipmentId, LocalDate assignmentDate, AssignmentStatus status, Long id);

    @Query("""
            select coalesce(sum(assignment.totalCost), 0)
            from EquipmentAssignment assignment
            where assignment.project.id = :projectId
            and assignment.status = com.example.internshipapp.common.enums.AssignmentStatus.VALIDATED
            """)
    BigDecimal sumValidatedCostByProjectId(@Param("projectId") Long projectId);
}
