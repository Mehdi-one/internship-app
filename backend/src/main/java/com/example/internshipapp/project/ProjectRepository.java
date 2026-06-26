package com.example.internshipapp.project;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.internshipapp.common.enums.ProjectStatus;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByReference(String reference);

    Optional<Project> findByReference(String reference);

    List<Project> findAllByOrderByUpdatedAtDesc();

    List<Project> findByStatusOrderByUpdatedAtDesc(ProjectStatus status);

    @Query("""
            select p from Project p
            where lower(p.reference) like lower(concat('%', :search, '%'))
               or lower(p.title) like lower(concat('%', :search, '%'))
               or lower(coalesce(p.clientName, '')) like lower(concat('%', :search, '%'))
               or lower(coalesce(p.responsibleName, '')) like lower(concat('%', :search, '%'))
            order by p.updatedAt desc
            """)
    List<Project> search(@Param("search") String search);

    @Query("""
            select p from Project p
            where p.status = :status
              and (lower(p.reference) like lower(concat('%', :search, '%'))
                   or lower(p.title) like lower(concat('%', :search, '%'))
                   or lower(coalesce(p.clientName, '')) like lower(concat('%', :search, '%'))
                   or lower(coalesce(p.responsibleName, '')) like lower(concat('%', :search, '%')))
            order by p.updatedAt desc
            """)
    List<Project> searchByStatus(@Param("search") String search, @Param("status") ProjectStatus status);
}
