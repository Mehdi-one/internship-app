package com.example.internshipapp.employee;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.internshipapp.common.enums.EmployeeStatus;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByRegistrationNumber(String registrationNumber);

    Optional<Employee> findByRegistrationNumber(String registrationNumber);

    List<Employee> findAllByOrderByUpdatedAtDesc();

    List<Employee> findByStatusOrderByUpdatedAtDesc(EmployeeStatus status);

    @Query("""
            select e from Employee e
            where lower(e.registrationNumber) like lower(concat('%', :search, '%'))
               or lower(e.fullName) like lower(concat('%', :search, '%'))
               or lower(e.qualification) like lower(concat('%', :search, '%'))
            order by e.updatedAt desc
            """)
    List<Employee> search(@Param("search") String search);

    @Query("""
            select e from Employee e
            where e.status = :status
              and (lower(e.registrationNumber) like lower(concat('%', :search, '%'))
                   or lower(e.fullName) like lower(concat('%', :search, '%'))
                   or lower(e.qualification) like lower(concat('%', :search, '%')))
            order by e.updatedAt desc
            """)
    List<Employee> searchByStatus(@Param("search") String search, @Param("status") EmployeeStatus status);
}
