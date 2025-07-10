package com.inmohouse.backend.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.inmohouse.backend.backend.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    // Búsqueda de usuarios cliente por token de agente
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.nombre = 'ROLE_CLIENTE'")
    List<User> findAllClientes();

    // Búsqueda de usuarios pertenecientes a un agente
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.nombre = 'ROLE_CLIENTE' AND u.agente.id = :agenteId")
    List<User> findClientesPorAgenteId(@Param("agenteId") Long agenteId);
}
