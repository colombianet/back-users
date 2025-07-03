package com.inmohouse.backend.backend.controllers;

import com.inmohouse.backend.backend.dto.PropiedadRequest;
import com.inmohouse.backend.backend.entities.Propiedad;
import com.inmohouse.backend.backend.repositories.PropiedadRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/propiedades")
public class PropiedadController {

    @Autowired
    private PropiedadRepository repository;

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_AGENTE')")
    @PostMapping
    public ResponseEntity<Propiedad> create(@RequestBody PropiedadRequest request) {
        Propiedad propiedad = new Propiedad(
            request.getTitulo(),
            request.getDescripcion(),
            request.getTipo(),
            request.getPrecio(),
            request.getUbicacion(),
            request.getEstado(),
            request.getAgenteId(),
            request.getClienteId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(this.repository.save(propiedad));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_AGENTE') or hasAuthority('ROLE_CLIENTE')")
    @GetMapping
    public ResponseEntity<Iterable<Propiedad>> list() {
        return ResponseEntity.ok(this.repository.findAll());
    }
}
