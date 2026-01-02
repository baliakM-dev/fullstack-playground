package com.todo.todo_app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "projects",
        uniqueConstraints = @UniqueConstraint(name = "uk_projects_name", columnNames = "name")
)
public class Project extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 2000)
    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Todo> todos = new ArrayList<>();
}
