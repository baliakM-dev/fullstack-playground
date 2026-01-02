package com.keycloak.keycloak_app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    public record AdminUserSummary(String username, String fullName, List<String> roles, String department) {
    }

    @GetMapping("/users")
    public List<AdminUserSummary> users() {
        return List.of(
                new AdminUserSummary("alice", "Alice Admin", List.of("admin"), "Platform"),
                new AdminUserSummary("henry", "Henry HR", List.of("hr"), "People"),
                new AdminUserSummary("martina", "Martina Manager", List.of("manager"), "Sales")
        );
    }
}