package com.inmohouse.backend.backend.controllers;

import com.inmohouse.backend.backend.repositories.PropiedadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/estadisticas")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class EstadisticasController {

    @Autowired
    private PropiedadRepository propiedadRepository;

    @GetMapping("/propiedades-por-tipo")
    public ResponseEntity<List<Map<String, Object>>> propiedadesPorTipo() {
        List<Object[]> resultados = propiedadRepository.contarPorTipo();
        List<Map<String, Object>> response = resultados.stream().map(row -> {
            Map<String, Object> item = new HashMap<>();
            item.put("tipo", row[0]);
            item.put("cantidad", row[1]);
            return item;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/propiedades-por-agente")
    public ResponseEntity<List<Map<String, Object>>> propiedadesPorAgente() {
        List<Object[]> resultados = propiedadRepository.contarPorAgente();
        List<Map<String, Object>> response = resultados.stream().map(row -> {
            Map<String, Object> item = new HashMap<>();
            item.put("agente", row[0]);
            item.put("cantidad", row[1]);
            return item;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
