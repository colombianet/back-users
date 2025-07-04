package com.inmohouse.backend.backend.controllers;

import com.inmohouse.backend.backend.dto.PropiedadRequest;
import com.inmohouse.backend.backend.entities.Propiedad;
import com.inmohouse.backend.backend.entities.User;
import com.inmohouse.backend.backend.repositories.PropiedadRepository;
import com.inmohouse.backend.backend.repositories.UserRepository;
import com.inmohouse.backend.backend.security.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/propiedades")
public class PropiedadController {

    @Autowired
    private PropiedadRepository repository;

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_AGENTE')")
    @PostMapping
    public ResponseEntity<Propiedad> create(@RequestBody PropiedadRequest request,
                                            Authentication authentication) {
        User agente;

        if (authentication != null) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            agente = userRepository.findById(userDetails.getId()).orElse(null);
        } else {
            agente = userRepository.findById(request.getAgenteId()).orElse(null);
        }

        if (agente == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Propiedad propiedad = new Propiedad(
                request.getTitulo(),
                request.getDescripcion(),
                request.getTipo(),
                request.getPrecio(),
                request.getUbicacion(),
                request.getEstado(),
                agente);

        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(propiedad));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_AGENTE', 'ROLE_CLIENTE')")
    @GetMapping
    public ResponseEntity<Iterable<Propiedad>> list() {
        return ResponseEntity.ok(repository.findAll());
    }

    // Estadísticas por tipo
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/estadisticas/tipo")
    public ResponseEntity<List<Map<String, Object>>> estadisticasPorTipo() {
        List<Object[]> resultados = repository.contarPorTipo();
        List<Map<String, Object>> response = resultados.stream().map(row -> {
            Map<String, Object> item = new HashMap<>();
            item.put("tipo", row[0]);
            item.put("cantidad", row[1]);
            return item;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Estadísticas por agente
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/estadisticas/agente")
    public ResponseEntity<List<Map<String, Object>>> estadisticasPorAgente() {
        List<Object[]> resultados = repository.contarPorAgente();
        List<Map<String, Object>> response = resultados.stream().map(row -> {
            Map<String, Object> item = new HashMap<>();
            item.put("agente", row[0]);
            item.put("cantidad", row[1]);
            return item;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
