package com.todo.todo_app.controller;

import com.todo.todo_app.dto.tag.*;
import com.todo.todo_app.mapper.TagMapper;
import com.todo.todo_app.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService service;
    private final TagMapper mapper;

    @GetMapping
    public List<TagResponse> list() {
        return service.list().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public TagResponse get(@PathVariable UUID id) {
        return mapper.toResponse(service.get(id));
    }

    @PostMapping
    public TagResponse create(@Valid @RequestBody TagCreateRequest req) {
        return mapper.toResponse(service.create(req));
    }

    @PutMapping("/{id}")
    public TagResponse update(@PathVariable UUID id, @Valid @RequestBody TagUpdateRequest req) {
        return mapper.toResponse(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}