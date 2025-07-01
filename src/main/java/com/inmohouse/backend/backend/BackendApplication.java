package com.inmohouse.backend.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.inmohouse.backend.backend.repositories.UserRepository;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner encriptarPasswords(UserRepository userRepository) {
		return args -> {
			userRepository.findAll().forEach(usuario -> {
				String plano = usuario.getPassword();
				if (!plano.startsWith("$2a$")) {
					String encriptada = BCrypt.hashpw(plano, BCrypt.gensalt());
					usuario.setPassword(encriptada);
					userRepository.save(usuario);
				}
			});
		};
	}
}
