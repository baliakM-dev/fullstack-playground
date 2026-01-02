package com.todo.todo_app.dto.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagCreateRequest(
        @NotBlank @Size(max = 60) String name
) {}