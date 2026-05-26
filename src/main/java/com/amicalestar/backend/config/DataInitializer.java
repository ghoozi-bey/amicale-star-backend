package com.amicalestar.backend.config;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.enums.Departement;
import com.amicalestar.backend.enums.TypeAdherent;
import com.amicalestar.backend.repositories.AdherentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    // Repository pour gérer les opérations sur les adhérents
    private final AdherentRepository adherentRepository;

    // Encodeur pour sécuriser le mot de passe
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initAdmin() {
        return args -> {

            // Vérifie si l'administrateur existe déjà
            if (adherentRepository.findByEmail("admin@test.com").isEmpty()) {

                // Création du compte administrateur par défaut
                Adherent admin = Adherent.builder()
                        .matricule("STAR000001")
                        .nom("Admin")
                        .prenom("Admin")
                        .email("admin@test.com")
                        .password(passwordEncoder.encode("123456"))
                        .cin("00000000")
                        .telephone("00000000")
                        .dateNaissance(new Date())
                        .departement(Departement.INFORMATIQUE)
                        .typeAdherent(TypeAdherent.ADMIN)
                        .actif(true)
                        .build();

                // Sauvegarde de l'administrateur dans la base de données
                adherentRepository.save(admin);

                System.out.println("✅ ADMIN créé !");
            }
        };
    }
}