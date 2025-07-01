package com.inmohouse.backend.backend.controllers;

import org.springframework.web.bind.annotation.RestController;
import com.inmohouse.backend.backend.entities.User;
import com.inmohouse.backend.backend.services.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserService service;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public List<User> list() {
        return this.service.findAll();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<User> userOptional = this.service.findbyId(id);
        if (userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(userOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("error", "Usuario no encontrado con id: " + id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(user));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
        Optional<User> userOptional = this.service.findbyId(id);
        if (userOptional.isPresent()) {
            User userBD = userOptional.get();
            userBD.setEmail(user.getEmail());
            userBD.setNombre(user.getNombre());
            userBD.setPassword(user.getPassword());
            return ResponseEntity.ok(this.service.save(userBD));
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<User> userOptional = this.service.findbyId(id);
        if (userOptional.isPresent()) {
            this.service.deletebyId(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
