package com.todo.todo_app.dto.todo;

import com.todo.todo_app.entity.TodoPriority;
import com.todo.todo_app.entity.TodoStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record TodoCreateRequest(
        @NotBlank @Size(max = 120) String title,
        @Size(max = 2000) String description,
        @NotNull TodoStatus status,
        @NotNull TodoPriority priority,
        LocalDate dueDate,
        @NotNull UUID projectId,
        Set<UUID> tagIds
) {}