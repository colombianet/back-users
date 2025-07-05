package com.inmohouse.backend.backend.controllers;

import com.inmohouse.backend.backend.entities.User;
import com.inmohouse.backend.backend.entities.Role;
import com.inmohouse.backend.backend.repositories.RoleRepository;
import com.inmohouse.backend.backend.services.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<User> list() {
        return service.findAll();
    }

    @PreAuthorize("hasRole('AGENTE')")
    @GetMapping("/clientes")
    public ResponseEntity<?> listarClientes() {
        List<User> clientes = service.findAllClientes();
        return ResponseEntity.ok(clientes);
    }

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

    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody User user) {
        if (!tieneRoles(user)) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "El usuario debe tener al menos un rol asignado."));
        }

        if (hasRole("AGENTE") && !esCliente(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Solo se pueden crear usuarios con rol CLIENTE"));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        List<Role> rolesEntrantes = user.getRoles();
        List<Role> rolesPersistidos = rolesEntrantes.stream()
                .map(r -> {
                    String nombreNormalizado = r.getNombre().startsWith("ROLE_")
                            ? r.getNombre()
                            : "ROLE_" + r.getNombre();

                    return roleRepository.findByNombre(nombreNormalizado)
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombreNormalizado));
                })
                .collect(Collectors.toList());

        user.setRoles(rolesPersistidos);

        User nuevo = service.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User user) {
        System.out.println("Password recibida: '" + user.getPassword() + "'");

        if (!tieneRoles(user)) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "El usuario debe tener al menos un rol asignado."));
        }

        Optional<User> userOptional = service.findbyId(id);
        if (userOptional.isPresent()) {
            User userBD = userOptional.get();

            if (hasRole("AGENTE") && !esCliente(userBD)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", "Solo se pueden editar usuarios con rol CLIENTE"));
            }

            userBD.setEmail(user.getEmail());
            userBD.setNombre(user.getNombre());

            // ✅ Solo actualiza la contraseña si viene con contenido real
            if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                userBD.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            // ✅ Actualiza los roles
            List<Role> rolesEntrantes = user.getRoles();
            List<Role> rolesPersistidos = rolesEntrantes.stream()
                    .map(r -> {
                        String nombreNormalizado = r.getNombre().startsWith("ROLE_")
                                ? r.getNombre()
                                : "ROLE_" + r.getNombre();

                        return roleRepository.findByNombre(nombreNormalizado)
                                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombreNormalizado));
                    })
                    .collect(Collectors.toList());

            userBD.setRoles(rolesPersistidos);

            return ResponseEntity.ok(service.save(userBD));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("error", "No se pudo actualizar, usuario no encontrado con id: " + id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<User> userOptional = service.findbyId(id);
        if (userOptional.isPresent()) {
            User userBD = userOptional.get();

            if (hasRole("AGENTE") && !esCliente(userBD)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", "Solo se pueden eliminar usuarios con rol CLIENTE"));
            }

            service.deletebyId(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("error", "No se pudo eliminar, usuario no encontrado con id: " + id));
    }

    // 🔒 Helpers

    private boolean esCliente(User user) {
        return user.getRoles().stream()
                .anyMatch(r -> r.getNombre().equals("ROLE_CLIENTE"));
    }

    private boolean tieneRoles(User user) {
        return user.getRoles() != null && !user.getRoles().isEmpty();
    }

    private boolean hasRole(String roleName) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equals("ROLE_" + roleName));
    }
}
