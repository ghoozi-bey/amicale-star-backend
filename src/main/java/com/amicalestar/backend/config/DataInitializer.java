package com.amicalestar.backend.config;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.enums.TypeAdherent;
import com.amicalestar.backend.repositories.AdherentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final AdherentRepository adherentRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initAdmin() {
        return args -> {

            if (adherentRepository.findByEmail("admin@test.com").isEmpty()) {

                Adherent admin = new Adherent();

                admin.setMatricule("STAR000001");
                admin.setNom("Admin");
                admin.setPrenom("Root");
                admin.setEmail("admin@test.com");

                // 🔐 IMPORTANT
                admin.setPassword(passwordEncoder.encode("123456"));

                admin.setTypeAdherent(TypeAdherent.ADMIN);

                adherentRepository.save(admin);

                System.out.println("✅ ADMIN créé !");
            }
        };
    }
}