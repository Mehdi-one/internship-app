package com.example.internshipapp.employee;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeCostHistoryRepository extends JpaRepository<EmployeeCostHistory, Long> {

    List<EmployeeCostHistory> findByEmployeeIdOrderByEffectiveDateDescCreatedAtDesc(Long employeeId);

    @Query(value = """
            select * from employee_cost_history
            where employee_id = :employeeId
              and effective_date <= :date
            order by effective_date desc, created_at desc
            limit 1
            """, nativeQuery = true)
    Optional<EmployeeCostHistory> findTopByEmployeeIdAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
            @Param("employeeId") Long employeeId,
            @Param("date") LocalDate date);
}
