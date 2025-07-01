package com.inmohouse.backend.backend.services;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.inmohouse.backend.backend.dto.LoginRequest;
import com.inmohouse.backend.backend.dto.LoginResponse;
import com.inmohouse.backend.backend.entities.User;
import com.inmohouse.backend.backend.repositories.UserRepository;
import com.inmohouse.backend.backend.security.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) throws Exception {

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            System.out.println("⚠ Usuario NO encontrado");
            throw new Exception("Usuario no encontrado");
        }

        User user = userOpt.get();
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new Exception("Contraseña incorrecta");
        }

        String token = jwtUtil.generateToken(user);
        return new LoginResponse(token);
    }

}
