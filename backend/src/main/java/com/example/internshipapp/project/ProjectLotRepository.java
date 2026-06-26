package com.example.internshipapp.project;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectLotRepository extends JpaRepository<ProjectLot, Long> {

    List<ProjectLot> findByProjectIdOrderByIdAsc(Long projectId);
}
