package com.inception.app.controller;

import java.util.Map;
import org.springframework.jdc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    private final JdbcTemplate jdbc;

    public HealthController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/health")
    public Map<String, Object> healthDb() {
        try {
            Integer one = jdbc.queryForObject("SELECT 1", Integer.class);
            Long count = jdbc.queryForObject("SELECT COUNT(*) FROM health_ckeck", Long.class);

            return Map.of(
                "dbOk", true,
                "select1", one,
                "healthCheckCount", count
            );
        } catch (Exception e) {
            return Map.of(
                "dbOk", false,
                "error", e.getClass().getSimpleName(),
                "message", String.valueOf(e.getMessage())
            );
        }
    }
}
