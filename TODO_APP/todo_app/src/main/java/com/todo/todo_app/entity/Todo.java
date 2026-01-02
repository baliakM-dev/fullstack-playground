package com.todo.todo_app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "todos")
public class Todo extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TodoStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TodoPriority priority;

    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_todos_project"))
    private Project project;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "todo_tags",
            joinColumns = @JoinColumn(name = "todo_id", foreignKey = @ForeignKey(name = "fk_todo_tags_todo")),
            inverseJoinColumns = @JoinColumn(name = "tag_id", foreignKey = @ForeignKey(name = "fk_todo_tags_tag"))
    )
    private Set<Tag> tags = new HashSet<>();
}
