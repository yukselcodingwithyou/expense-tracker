package com.expensetracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "Application health and monitoring APIs")
public class HealthController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/detailed")
    @Operation(summary = "Detailed health check", description = "Get detailed health status of all components")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("timestamp", Instant.now());
        health.put("status", "UP");
        health.put("components", getComponentsHealth());
        
        return ResponseEntity.ok(health);
    }

    @GetMapping("/metrics")
    @Operation(summary = "Basic metrics", description = "Get basic application metrics")
    public ResponseEntity<Map<String, Object>> metrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("timestamp", Instant.now());
        
        // Database metrics
        try {
            long userCount = mongoTemplate.getCollection("users").estimatedDocumentCount();
            long familyCount = mongoTemplate.getCollection("families").estimatedDocumentCount();
            long transactionCount = mongoTemplate.getCollection("ledgerEntry").estimatedDocumentCount();
            
            Map<String, Object> dbMetrics = new HashMap<>();
            dbMetrics.put("users", userCount);
            dbMetrics.put("families", familyCount);
            dbMetrics.put("transactions", transactionCount);
            metrics.put("database", dbMetrics);
        } catch (Exception e) {
            metrics.put("database", Map.of("status", "error", "message", e.getMessage()));
        }

        // Redis metrics
        try {
            String redisInfo = redisTemplate.getConnectionFactory().getConnection().info().getProperty("used_memory_human");
            metrics.put("redis", Map.of("memory_usage", redisInfo, "status", "connected"));
        } catch (Exception e) {
            metrics.put("redis", Map.of("status", "error", "message", e.getMessage()));
        }

        // System metrics
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> systemMetrics = new HashMap<>();
        systemMetrics.put("memory_total_mb", runtime.totalMemory() / 1024 / 1024);
        systemMetrics.put("memory_free_mb", runtime.freeMemory() / 1024 / 1024);
        systemMetrics.put("memory_used_mb", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024);
        systemMetrics.put("processors", runtime.availableProcessors());
        metrics.put("system", systemMetrics);

        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/ready")
    @Operation(summary = "Readiness check", description = "Check if application is ready to serve requests")
    public ResponseEntity<Map<String, String>> readiness() {
        Map<String, Object> components = getComponentsHealth();
        
        boolean allHealthy = components.values().stream()
                .allMatch(component -> {
                    if (component instanceof Map) {
                        return "UP".equals(((Map<?, ?>) component).get("status"));
                    }
                    return false;
                });

        if (allHealthy) {
            return ResponseEntity.ok(Map.of(
                    "status", "READY",
                    "timestamp", Instant.now().toString()
            ));
        } else {
            return ResponseEntity.status(503).body(Map.of(
                    "status", "NOT_READY",
                    "timestamp", Instant.now().toString()
            ));
        }
    }

    @GetMapping("/live")
    @Operation(summary = "Liveness check", description = "Check if application is alive")
    public ResponseEntity<Map<String, String>> liveness() {
        return ResponseEntity.ok(Map.of(
                "status", "ALIVE",
                "timestamp", Instant.now().toString()
        ));
    }

    private Map<String, Object> getComponentsHealth() {
        Map<String, Object> components = new HashMap<>();

        // MongoDB health
        try {
            mongoTemplate.getDb().runCommand(org.bson.Document.parse("{ ping: 1 }"));
            components.put("mongodb", Map.of("status", "UP"));
        } catch (Exception e) {
            components.put("mongodb", Map.of("status", "DOWN", "error", e.getMessage()));
        }

        // Redis health
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            components.put("redis", Map.of("status", "UP"));
        } catch (Exception e) {
            components.put("redis", Map.of("status", "DOWN", "error", e.getMessage()));
        }

        return components;
    }
}