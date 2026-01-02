package com.keycloak.keycloak_app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/hr")
public class HrController {

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        return Map.of(
                "message", "HR data protected by the BFF",
                "activeEmployees", 42,
                "openPositions", 3,
                "payrollClosed", true
        );
    }
}