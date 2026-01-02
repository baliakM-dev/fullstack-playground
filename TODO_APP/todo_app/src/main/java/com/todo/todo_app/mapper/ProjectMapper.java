package com.todo.todo_app.mapper;

import com.todo.todo_app.dto.project.ProjectResponse;
import com.todo.todo_app.entity.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectResponse toResponse(Project entity);
}