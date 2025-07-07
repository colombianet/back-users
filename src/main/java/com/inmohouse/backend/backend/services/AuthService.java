package com.inmohouse.backend.backend.services;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            System.out.println("⚠ Usuario NO encontrado.");
            throw new Exception("Usuario no encontrado");
        }

        User user = userOpt.get();

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        System.out.println("🔐 Comparando contraseñas: " + passwordMatches);

        if (!passwordMatches) {
            throw new Exception("Contraseña incorrecta");
        }

        String token = jwtUtil.generateToken(user);
        System.out.println("✅ Token generado para: " + user.getEmail());

        return new LoginResponse(token);
    }
}
