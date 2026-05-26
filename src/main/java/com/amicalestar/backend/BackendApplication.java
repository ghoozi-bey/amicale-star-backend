package com.amicalestar.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class BackendApplication {

	// === Point d’entrée de l’application Spring Boot ===
	public static void main(String[] args) {

		SpringApplication.run(
				BackendApplication.class,
				args
		);

		// Message console démarrage backend
		System.out.println(
				"Backend is running 🚀"
		);
	}

}