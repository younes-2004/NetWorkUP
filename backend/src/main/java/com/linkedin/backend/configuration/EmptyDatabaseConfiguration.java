package com.linkedin.backend.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod") // Activé uniquement avec le profil "prod"
public class EmptyDatabaseConfiguration {
    // Cette classe ne contient aucun CommandLineRunner
    // Aucune donnée ne sera chargée en mode production
}