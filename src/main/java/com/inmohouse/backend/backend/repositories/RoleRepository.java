package com.inmohouse.backend.backend.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import com.inmohouse.backend.backend.entities.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByNombre(String nombre);
}
