package org.example.colis.config;

import org.example.colis.enums.Role;
import org.example.colis.enums.Specialite;
import org.example.colis.enums.StatutTransporteur;
import org.example.colis.model.User;
import org.example.colis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Create default admin if not exists
        if (userRepository.findByLogin("admin").isEmpty()) {
            User admin = new User();
            admin.setLogin("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);
            System.out.println("Default ADMIN created: login=admin, password=admin123");
        }
        
        // Create default transporteurs for each speciality if not exists
        if (userRepository.findByLogin("transporteur_standard").isEmpty()) {
            User transporteur1 = new User();
            transporteur1.setLogin("transporteur_standard");
            transporteur1.setPassword(passwordEncoder.encode("trans123"));
            transporteur1.setRole(Role.TRANSPORTEUR);
            transporteur1.setSpecialite(Specialite.STANDARD);
            transporteur1.setStatut(StatutTransporteur.DISPONIBLE);
            transporteur1.setActive(true);
            userRepository.save(transporteur1);
            System.out.println("Default TRANSPORTEUR STANDARD created: login=transporteur_standard, password=trans123");
        }
        
        if (userRepository.findByLogin("transporteur_fragile").isEmpty()) {
            User transporteur2 = new User();
            transporteur2.setLogin("transporteur_fragile");
            transporteur2.setPassword(passwordEncoder.encode("trans123"));
            transporteur2.setRole(Role.TRANSPORTEUR);
            transporteur2.setSpecialite(Specialite.FRAGILE);
            transporteur2.setStatut(StatutTransporteur.DISPONIBLE);
            transporteur2.setActive(true);
            userRepository.save(transporteur2);
            System.out.println("Default TRANSPORTEUR FRAGILE created: login=transporteur_fragile, password=trans123");
        }
        
        if (userRepository.findByLogin("transporteur_frigo").isEmpty()) {
            User transporteur3 = new User();
            transporteur3.setLogin("transporteur_frigo");
            transporteur3.setPassword(passwordEncoder.encode("trans123"));
            transporteur3.setRole(Role.TRANSPORTEUR);
            transporteur3.setSpecialite(Specialite.FRIGO);
            transporteur3.setStatut(StatutTransporteur.DISPONIBLE);
            transporteur3.setActive(true);
            userRepository.save(transporteur3);
            System.out.println("Default TRANSPORTEUR FRIGO created: login=transporteur_frigo, password=trans123");
        }
    }
}
