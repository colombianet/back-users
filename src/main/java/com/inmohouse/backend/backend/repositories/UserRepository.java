package com.inmohouse.backend.backend.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.inmohouse.backend.backend.entities.User;

public interface UserRepository extends CrudRepository<User, Long>{
    Optional<User> findByUser(String nombre);
}
