package com.inmohouse.backend.backend.security;

import com.inmohouse.backend.backend.repositories.UserRepository;
import com.inmohouse.backend.backend.entities.User;

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
                System.out.println("🌀 JwtAuthFilter ejecutado para: " + request.getRequestURI());


        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.isTokenValid(token)) {
                String email = jwtUtil.extractUsername(token);
                User user = userRepository.findByEmailWithRoles(email).orElse(null);
                System.out.println("📦 Roles en User desde JPA → " + user.getRoles());


                if (user != null) {
                    CustomUserDetails userDetails = new CustomUserDetails(user);
                    System.out.println("🚨 Authorities reconocidas: " + userDetails.getAuthorities());
                System.out.println("🚨 Authorities reconocidas: " + userDetails.getAuthorities());

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("🎯 Autoridades en contexto → " +
    SecurityContextHolder.getContext().getAuthentication().getAuthorities());

                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
