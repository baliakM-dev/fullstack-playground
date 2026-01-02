package com.todo.todo_app.repository;

import com.todo.todo_app.entity.Todo;
import com.todo.todo_app.entity.TodoStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TodoRepository extends JpaRepository<Todo, UUID> {

    @EntityGraph(attributePaths = {"project", "tags"})
    List<Todo> findAll();

    @EntityGraph(attributePaths = {"project", "tags"})
    List<Todo> findByProjectId(UUID projectId);

    @EntityGraph(attributePaths = {"project", "tags"})
    List<Todo> findByProjectIdAndStatus(UUID projectId, TodoStatus status);
}