package com.todo.todo_app.service;

import com.todo.todo_app.dto.project.ProjectCreateRequest;
import com.todo.todo_app.dto.project.ProjectUpdateRequest;
import com.todo.todo_app.entity.Project;
import com.todo.todo_app.exception.ConflictException;
import com.todo.todo_app.exception.NotFoundException;
import com.todo.todo_app.repository.ProjectRepository;
import com.todo.todo_app.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TodoRepository todoRepository;

    public List<Project> list() {
        return projectRepository.findAll();
    }

    public Project get(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Project not found: " + id));
    }

    @Transactional
    public Project create(ProjectCreateRequest req) {
        if (projectRepository.existsByNameIgnoreCase(req.name())) {
            throw new ConflictException("Project name already exists: " + req.name());
        }
        Project p = Project.builder()
                .name(req.name())
                .description(req.description())
                .build();
        return projectRepository.save(p);
    }

    @Transactional
    public Project update(UUID id, ProjectUpdateRequest req) {
        Project p = get(id);
        if (!p.getName().equalsIgnoreCase(req.name()) && projectRepository.existsByNameIgnoreCase(req.name())) {
            throw new ConflictException("Project name already exists: " + req.name());
        }
        p.setName(req.name());
        p.setDescription(req.description());
        return p;
    }

    @Transactional
    public void delete(UUID id) {
        // realistické pravidlo: project sa nedá zmazať ak má todos
        if (!todoRepository.findByProjectId(id).isEmpty()) {
            throw new ConflictException("Cannot delete project with existing todos");
        }
        projectRepository.delete(get(id));
    }
}