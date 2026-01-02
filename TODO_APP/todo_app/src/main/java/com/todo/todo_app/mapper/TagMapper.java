package com.todo.todo_app.mapper;

import com.todo.todo_app.dto.tag.TagResponse;
import com.todo.todo_app.entity.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagResponse toResponse(Tag entity);
}