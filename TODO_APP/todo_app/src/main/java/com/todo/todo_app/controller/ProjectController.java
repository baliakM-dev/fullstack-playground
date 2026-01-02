package com.todo.todo_app.controller;

import com.todo.todo_app.dto.project.*;
import com.todo.todo_app.mapper.ProjectMapper;
import com.todo.todo_app.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService service;
    private final ProjectMapper mapper;

    @GetMapping
    public List<ProjectResponse> list() {
        return service.list().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ProjectResponse get(@PathVariable UUID id) {
        return mapper.toResponse(service.get(id));
    }

    @PostMapping
    public ProjectResponse create(@Valid @RequestBody ProjectCreateRequest req) {
        return mapper.toResponse(service.create(req));
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable UUID id, @Valid @RequestBody ProjectUpdateRequest req) {
        return mapper.toResponse(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}