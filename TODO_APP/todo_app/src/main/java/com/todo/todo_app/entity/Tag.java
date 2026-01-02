package com.todo.todo_app.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "tags",
        uniqueConstraints = @UniqueConstraint(name = "uk_tags_name", columnNames = "name")
)
public class Tag extends BaseEntity {

    @Column(nullable = false, length = 60)
    private String name;
}
