package com.inmohouse.backend.backend.services;

import java.util.List;
import java.util.Optional;

import com.inmohouse.backend.backend.entities.User;

public interface UserService {
    List<User>findAll();

    Optional<User> findbyId(Long id);
    
    User save(User user);

    void deletebyId(Long id);
}
