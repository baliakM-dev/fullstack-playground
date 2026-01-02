package com.todo.todo_app.service;

import com.todo.todo_app.dto.todo.TodoCreateRequest;
import com.todo.todo_app.dto.todo.TodoUpdateRequest;
import com.todo.todo_app.entity.Project;
import com.todo.todo_app.entity.Tag;
import com.todo.todo_app.entity.Todo;
import com.todo.todo_app.entity.TodoStatus;
import com.todo.todo_app.exception.NotFoundException;
import com.todo.todo_app.repository.ProjectRepository;
import com.todo.todo_app.repository.TagRepository;
import com.todo.todo_app.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final ProjectRepository projectRepository;
    private final TagRepository tagRepository;

    public List<Todo> list(UUID projectId, TodoStatus status) {
        if (projectId == null) return todoRepository.findAll();
        if (status == null) return todoRepository.findByProjectId(projectId);
        return todoRepository.findByProjectIdAndStatus(projectId, status);
    }

    public Todo get(UUID id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Todo not found: " + id));
    }

    @Transactional
    public Todo create(TodoCreateRequest req) {
        Project project = projectRepository.findById(req.projectId())
                .orElseThrow(() -> new NotFoundException("Project not found: " + req.projectId()));

        Set<Tag> tags = resolveTags(req.tagIds());

        Todo todo = Todo.builder()
                .title(req.title())
                .description(req.description())
                .status(req.status())
                .priority(req.priority())
                .dueDate(req.dueDate())
                .project(project)
                .tags(tags)
                .build();

        return todoRepository.save(todo);
    }

    @Transactional
    public Todo update(UUID id, TodoUpdateRequest req) {
        Todo todo = get(id);

        Project project = projectRepository.findById(req.projectId())
                .orElseThrow(() -> new NotFoundException("Project not found: " + req.projectId()));

        todo.setTitle(req.title());
        todo.setDescription(req.description());
        todo.setStatus(req.status());
        todo.setPriority(req.priority());
        todo.setDueDate(req.dueDate());
        todo.setProject(project);

        todo.getTags().clear();
        todo.getTags().addAll(resolveTags(req.tagIds()));

        return todo;
    }

    @Transactional
    public void delete(UUID id) {
        todoRepository.delete(get(id));
    }

    private Set<Tag> resolveTags(Set<UUID> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return new HashSet<>();
        List<Tag> found = tagRepository.findAllById(tagIds);
        if (found.size() != tagIds.size()) {
            throw new NotFoundException("One or more tags not found");
        }
        return new HashSet<>(found);
    }
}