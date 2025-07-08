package com.inmohouse.backend.backend.controllers;

import com.inmohouse.backend.backend.entities.Propiedad;
import com.inmohouse.backend.backend.entities.User;
import com.inmohouse.backend.backend.services.PropiedadService;
import com.inmohouse.backend.backend.services.UserService;
import com.inmohouse.backend.backend.security.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agente")
@PreAuthorize("hasRole('AGENTE')")
public class AgenteController {

    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private UserService userService;

    // 🔍 Ver propiedades asociadas al agente actual.
    @GetMapping("/mis-propiedades")
    public ResponseEntity<List<Propiedad>> verMisPropiedades(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long agenteId = userDetails.getId();
        List<Propiedad> propiedades = propiedadService.findByAgenteId(agenteId);
        return ResponseEntity.ok(propiedades);
    }

    // 🏠 Crear nueva propiedad y asociarla al agente actual
    @PostMapping("/crear-propiedad")
    public ResponseEntity<Propiedad> crearPropiedad(@RequestBody Propiedad propiedad,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User agente = userService.findbyId(userDetails.getId()).orElseThrow();
        propiedad.setAgente(agente);
        Propiedad nueva = propiedadService.save(propiedad);
        return ResponseEntity.ok(nueva);
    }
}
