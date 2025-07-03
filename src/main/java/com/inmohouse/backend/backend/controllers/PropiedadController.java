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

@RestController
@RequestMapping("/api/propiedades")
public class PropiedadController {

    @Autowired
    private PropiedadRepository repository;

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    @PostMapping
    public ResponseEntity<Propiedad> create(@RequestBody PropiedadRequest request,
            Authentication authentication) {
        User agente;

        if (authentication != null) {
            // Extraer el agente desde el token
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            agente = userRepository.findById(userDetails.getId()).orElse(null);
        } else {
            // Fallback: usar agenteId solo si fue enviado (caso ADMIN creando en nombre de
            // otro)
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

    // Todos los roles pueden ver propiedades
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE', 'CLIENTE')")
    @GetMapping
    public ResponseEntity<Iterable<Propiedad>> list() {
        return ResponseEntity.ok(repository.findAll());
    }
}
