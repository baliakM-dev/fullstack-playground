package com.todo.todo_app.dto.todo;

import com.todo.todo_app.entity.TodoPriority;
import com.todo.todo_app.entity.TodoStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record TodoResponse(
        UUID id,
        String title,
        String description,
        TodoStatus status,
        TodoPriority priority,
        LocalDate dueDate,
        UUID projectId,
        String projectName,
        Set<TagMini> tags,
        Instant createdAt,
        Instant updatedAt
) {
    public record TagMini(UUID id, String name) {}
}