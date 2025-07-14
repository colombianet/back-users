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

    // 🏠 Crear propiedad (solo ADMIN), con opción de asignar agente si estado es
    // DISPONIBLE
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Propiedad> create(@RequestBody PropiedadRequest request) {
        Propiedad propiedad = new Propiedad(
                request.getTitulo(),
                request.getDescripcion(),
                request.getTipo(),
                request.getPrecio(),
                request.getUbicacion(),
                request.getEstado(),
                null // agente se evalúa abajo
        );

        if ("DISPONIBLE".equalsIgnoreCase(request.getEstado()) && request.getAgenteId() != null) {
            User agente = userRepository.findById(request.getAgenteId()).orElse(null);
            if (agente != null) {
                propiedad.setAgente(agente);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(propiedad));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_AGENTE', 'ROLE_CLIENTE')")
    @GetMapping
    public ResponseEntity<Iterable<Propiedad>> list() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ✏️ Actualizar propiedad, desasignar agente si estado ≠ DISPONIBLE
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_AGENTE')")
    public ResponseEntity<Propiedad> update(@PathVariable Long id, @RequestBody Propiedad propiedadActualizada) {
        return repository.findById(id)
                .map(propiedadExistente -> {
                    propiedadExistente.setTitulo(propiedadActualizada.getTitulo());
                    propiedadExistente.setDescripcion(propiedadActualizada.getDescripcion());
                    propiedadExistente.setTipo(propiedadActualizada.getTipo());
                    propiedadExistente.setEstado(propiedadActualizada.getEstado());
                    propiedadExistente.setUbicacion(propiedadActualizada.getUbicacion());
                    propiedadExistente.setPrecio(propiedadActualizada.getPrecio());

                    // 🚫 Desasignar agente si estado ≠ DISPONIBLE
                    if (!"DISPONIBLE".equalsIgnoreCase(propiedadActualizada.getEstado())) {
                        propiedadExistente.setAgente(null);
                    }

                    return ResponseEntity.ok(repository.save(propiedadExistente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 📊 Estadísticas por tipo
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/estadisticas/tipo")
    public ResponseEntity<List<Map<String, Object>>> estadisticasPorTipo() {
        List<String> tiposEsperados = List.of("Apartamento", "Casa", "Local");

        List<Object[]> resultados = repository.contarPorTipo(); // tipo, cantidad

        Map<String, Long> conteoActual = resultados.stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> ((Number) r[1]).longValue()));

        List<Map<String, Object>> response = tiposEsperados.stream().map(tipo -> {
            Map<String, Object> item = new HashMap<>();
            item.put("tipo", tipo);
            item.put("cantidad", conteoActual.getOrDefault(tipo, 0L));
            return item;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // 📊 Estadísticas por agente con propiedades asignadas
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

    // 📊 Estadísticas por agente con y sin propiedades asignadas
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/estadisticas/agente-completo")
    public ResponseEntity<List<Map<String, Object>>> estadisticasPorAgenteCompleto() {
        List<Object[]> resultados = repository.contarPorAgenteIncluyendoSinPropiedades();
        List<Map<String, Object>> response = resultados.stream().map(row -> {
            Map<String, Object> item = new HashMap<>();
            item.put("agente", row[0]);
            item.put("cantidad", row[1]);
            return item;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 🎯 Asignar agente solo si propiedad está DISPONIBLE
    @PutMapping("/{id}/asignar-agente")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> asignarAgente(@PathVariable Long id, @RequestBody Map<String, Long> payload) {
        Long nuevoAgenteId = payload.get("agenteId");

        Optional<Propiedad> propiedadOpt = repository.findByIdWithAgente(id);
        Optional<User> agenteOpt = userRepository.findByIdWithRoles(nuevoAgenteId);

        if (propiedadOpt.isEmpty() || agenteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Propiedad o agente no encontrado"));
        }

        Propiedad propiedad = propiedadOpt.get();

        if (!"DISPONIBLE".equalsIgnoreCase(propiedad.getEstado())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Solo se pueden asignar agentes a propiedades disponibles"));
        }

        propiedad.setAgente(agenteOpt.get());
        repository.save(propiedad);
        return ResponseEntity.ok(Map.of("mensaje", "Agente asignado correctamente"));
    }

    // 🚫 Desasignar agente manualmente
    @PutMapping("/{id}/desasignar-agente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> desasignarAgente(@PathVariable Long id) {
        Optional<Propiedad> optionalPropiedad = repository.findById(id);

        if (optionalPropiedad.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Propiedad no encontrada");
        }

        Propiedad propiedad = optionalPropiedad.get();

        if (propiedad.getAgente() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("La propiedad ya no tiene agente asignado");
        }

        propiedad.setAgente(null);
        repository.save(propiedad);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/debug/{id}")
    public ResponseEntity<?> debugPropiedad(@PathVariable Long id) {
        Optional<Propiedad> propiedadOpt = repository.findByIdWithAgente(id);
        if (propiedadOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se encontró la propiedad con id " + id));
        }

        Propiedad p = propiedadOpt.get();
        String nombreAgente = (p.getAgente() != null) ? p.getAgente().getNombre() : null;

        Map<String, Object> response = new HashMap<>();
        response.put("id", p.getId());
        response.put("estado", p.getEstado());
        response.put("titulo", p.getTitulo());
        response.put("agente", nombreAgente);

        return ResponseEntity.ok(response);
    }
}
