package com.todo.todo_app.repository;

import com.todo.todo_app.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    boolean existsByNameIgnoreCase(String name);
}