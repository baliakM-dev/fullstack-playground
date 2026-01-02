package com.todo.todo_app.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectCreateRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 2000) String description
) {}