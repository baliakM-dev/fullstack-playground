package com.todo.todo_app.mapper;

import com.todo.todo_app.dto.todo.TodoResponse;
import com.todo.todo_app.entity.Todo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = Collectors.class)
public interface TodoMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "tags", expression = "java(todo.getTags().stream().map(t -> new TodoResponse.TagMini(t.getId(), t.getName())).collect(Collectors.toSet()))")
    TodoResponse toResponse(Todo todo);
}
