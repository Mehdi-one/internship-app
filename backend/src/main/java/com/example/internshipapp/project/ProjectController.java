package com.example.internshipapp.project;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.internshipapp.common.enums.ProjectStatus;
import com.example.internshipapp.project.dto.ProjectRequest;
import com.example.internshipapp.project.dto.ProjectResponse;
import com.example.internshipapp.project.dto.ProjectSummaryResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ProjectResponse create(@Valid @RequestBody ProjectRequest request) {
        return projectService.create(request);
    }

    @GetMapping
    public List<ProjectResponse> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ProjectStatus status) {
        return projectService.findAll(search, status);
    }

    @GetMapping("/{id}")
    public ProjectResponse findById(@PathVariable Long id) {
        return projectService.findById(id);
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return projectService.update(id, request);
    }

    @PatchMapping("/{id}/close")
    public ProjectResponse close(@PathVariable Long id) {
        return projectService.close(id);
    }

    @PatchMapping("/{id}/archive")
    public ProjectResponse archive(@PathVariable Long id) {
        return projectService.archive(id);
    }

    @GetMapping("/{id}/summary")
    public ProjectSummaryResponse getSummary(@PathVariable Long id) {
        return projectService.getSummary(id);
    }
}
