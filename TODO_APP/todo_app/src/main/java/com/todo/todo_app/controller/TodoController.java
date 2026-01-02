package com.todo.todo_app.controller;

import com.todo.todo_app.dto.todo.*;
import com.todo.todo_app.entity.TodoStatus;
import com.todo.todo_app.mapper.TodoMapper;
import com.todo.todo_app.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService service;
    private final TodoMapper mapper;

    @GetMapping
    public List<TodoResponse> list(
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) TodoStatus status
    ) {
        return service.list(projectId, status).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public TodoResponse get(@PathVariable UUID id) {
        return mapper.toResponse(service.get(id));
    }

    @PostMapping
    public TodoResponse create(@Valid @RequestBody TodoCreateRequest req) {
        return mapper.toResponse(service.create(req));
    }

    @PutMapping("/{id}")
    public TodoResponse update(@PathVariable UUID id, @Valid @RequestBody TodoUpdateRequest req) {
        return mapper.toResponse(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}