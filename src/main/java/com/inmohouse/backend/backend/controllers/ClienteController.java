package com.inmohouse.backend.backend.controllers;

import com.inmohouse.backend.backend.entities.Propiedad;
import com.inmohouse.backend.backend.repositories.PropiedadRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cliente")
@PreAuthorize("hasRole('CLIENTE')")
public class ClienteController {

    @Autowired
    private PropiedadRepository propiedadRepository;

    // 🔍 Ver propiedades disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<Propiedad>> verPropiedadesDisponibles() {
        List<Propiedad> disponibles = propiedadRepository.findByEstado("DISPONIBLE");
        return ResponseEntity.ok(disponibles);
    }
}
