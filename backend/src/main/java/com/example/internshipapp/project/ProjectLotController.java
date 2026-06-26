package com.example.internshipapp.project;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.internshipapp.project.dto.ProjectLotRequest;
import com.example.internshipapp.project.dto.ProjectLotResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ProjectLotController {

    private final ProjectLotService projectLotService;

    public ProjectLotController(ProjectLotService projectLotService) {
        this.projectLotService = projectLotService;
    }

    @PostMapping("/projects/{projectId}/lots")
    public ProjectLotResponse create(@PathVariable Long projectId, @Valid @RequestBody ProjectLotRequest request) {
        return projectLotService.create(projectId, request);
    }

    @GetMapping("/projects/{projectId}/lots")
    public List<ProjectLotResponse> findByProject(@PathVariable Long projectId) {
        return projectLotService.findByProject(projectId);
    }

    @PutMapping("/project-lots/{id}")
    public ProjectLotResponse update(@PathVariable Long id, @Valid @RequestBody ProjectLotRequest request) {
        return projectLotService.update(id, request);
    }

    @PatchMapping("/project-lots/{id}/archive")
    public ProjectLotResponse archive(@PathVariable Long id) {
        return projectLotService.archive(id);
    }
}
