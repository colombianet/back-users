package com.inmohouse.backend.backend.security;

import com.inmohouse.backend.backend.entities.User;
import com.inmohouse.backend.backend.repositories.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.isTokenValid(token)) {
                String email = jwtUtil.extractUsername(token);

                // ✅ Cargar usuario con roles desde la base de datos
                User user = userRepository.findByEmailWithRoles(email).orElse(null);

                if (user != null) {
                    CustomUserDetails userDetails = new CustomUserDetails(user);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    System.out.println("✅ Usuario autenticado: " + email);
                    userDetails.getAuthorities().forEach(a -> System.out.println("Rol: " + a.getAuthority()));
                } else {
                    System.out.println("❌ Usuario no encontrado en la base de datos: " + email);
                }
            } else {
                System.out.println("❌ Token inválido");
            }
        }

        filterChain.doFilter(request, response);
    }
}
