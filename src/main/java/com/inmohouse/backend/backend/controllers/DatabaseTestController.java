package com.inmohouse.backend.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
public class DatabaseTestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/api/test-db")
    public ResponseEntity<String> testDbConnection() {
        try (Connection conn = dataSource.getConnection()) {
            return ResponseEntity.ok("✅ Conexión exitosa con la base de datos");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error de conexión: " + e.getMessage());
        }
    }
}
