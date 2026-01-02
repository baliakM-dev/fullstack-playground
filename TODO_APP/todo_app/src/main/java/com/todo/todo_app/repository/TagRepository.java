package com.todo.todo_app.repository;

import com.todo.todo_app.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    boolean existsByNameIgnoreCase(String name);
}