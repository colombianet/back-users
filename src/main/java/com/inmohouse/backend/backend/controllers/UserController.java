package com.inmohouse.backend.backend.controllers;

import com.inmohouse.backend.backend.entities.User;
import com.inmohouse.backend.backend.services.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 📋 Solo admin puede listar todos los usuarios
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<User> list() {
        return service.findAll();
    }

    // 🔎 Admin puede ver cualquier usuario, AGENTE su propio perfil
    @PreAuthorize("hasRole('ADMIN') or (#id == principal.id and hasRole('AGENTE'))")
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<User> userOptional = service.findbyId(id);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("error", "Usuario no encontrado con id: " + id));
    }

    // ➕ Solo admin puede crear usuarios
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(user));
    }

    // 🖊️ Admin edita cualquiera, AGENTE solo su perfil
    @PreAuthorize("hasRole('ADMIN') or (#id == principal.id and hasRole('AGENTE'))")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User user) {
        Optional<User> userOptional = service.findbyId(id);
        if (userOptional.isPresent()) {
            User userBD = userOptional.get();
            userBD.setEmail(user.getEmail());
            userBD.setNombre(user.getNombre());

            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                userBD.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            return ResponseEntity.ok(service.save(userBD));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("error", "No se pudo actualizar, usuario no encontrado con id: " + id));
    }

    // Solo admin puede eliminar usuarios
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<User> userOptional = service.findbyId(id);
        if (userOptional.isPresent()) {
            service.deletebyId(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("error", "No se pudo eliminar, usuario no encontrado con id: " + id));
    }
}
