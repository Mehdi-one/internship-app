package com.example.internshipapp.project;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internshipapp.common.exception.ResourceNotFoundException;
import com.example.internshipapp.project.dto.ProjectLotRequest;
import com.example.internshipapp.project.dto.ProjectLotResponse;

@Service
public class ProjectLotService {

    private final ProjectLotRepository projectLotRepository;
    private final ProjectService projectService;

    public ProjectLotService(ProjectLotRepository projectLotRepository, ProjectService projectService) {
        this.projectLotRepository = projectLotRepository;
        this.projectService = projectService;
    }

    @Transactional
    public ProjectLotResponse create(Long projectId, ProjectLotRequest request) {
        ProjectLot lot = new ProjectLot();
        lot.setProject(projectService.getProject(projectId));
        fillLot(lot, request);
        return toResponse(projectLotRepository.save(lot));
    }

    @Transactional(readOnly = true)
    public List<ProjectLotResponse> findByProject(Long projectId) {
        return projectLotRepository.findByProjectIdOrderByIdAsc(projectId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ProjectLotResponse update(Long id, ProjectLotRequest request) {
        ProjectLot lot = getLot(id);
        fillLot(lot, request);
        return toResponse(projectLotRepository.save(lot));
    }

    @Transactional
    public ProjectLotResponse archive(Long id) {
        ProjectLot lot = getLot(id);
        lot.setArchived(true);
        return toResponse(projectLotRepository.save(lot));
    }

    private ProjectLot getLot(Long id) {
        return projectLotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project lot not found"));
    }

    private void fillLot(ProjectLot lot, ProjectLotRequest request) {
        lot.setDesignation(request.designation());
        lot.setQuantity(request.quantity());
        lot.setUnitPrice(request.unitPrice());
        lot.setPlannedAmount(request.plannedAmount());
    }

    private ProjectLotResponse toResponse(ProjectLot lot) {
        return new ProjectLotResponse(
                lot.getId(),
                lot.getProject().getId(),
                lot.getDesignation(),
                lot.getQuantity(),
                lot.getUnitPrice(),
                lot.getPlannedAmount(),
                lot.isArchived(),
                lot.getCreatedAt(),
                lot.getUpdatedAt());
    }
}
