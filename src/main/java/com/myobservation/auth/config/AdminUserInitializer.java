package com.myobservation.auth.config;

import com.myobservation.auth.entity.MyUser;
import com.myobservation.auth.entity.Role;
import com.myobservation.auth.repository.MyUserRepository;
import com.myobservation.auth.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final MyUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(MyUserRepository userRepository,
                                RoleRepository roleRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Crear roles si no existen
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        Role practitionerRole = roleRepository.findByName("ROLE_PRACTITIONER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_PRACTITIONER")));

        // Crear usuario admin si no existe
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            MyUser admin = new MyUser();
            admin.setFirstName("Admin");
            admin.setLastName("System");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(adminRole)); // Asignar rol ADMIN

            userRepository.save(admin);
            System.out.println("Usuario admin creado exitosamente");
        }
        /* Crear usuario practitioner
        if (userRepository.findByEmail("practitioner@example.com").isEmpty()) {
            MyUser practitioner = new MyUser();
            practitioner.setFirstName("Dr.");
            practitioner.setLastName("House");
            practitioner.setEmail("practitioner@example.com");
            practitioner.setPassword(passwordEncoder.encode("practitioner123"));
            practitioner.setRoles(Set.of(practitionerRole)); // Asignar rol PRACTITIONER

            userRepository.save(practitioner);
            System.out.println("Usuario practitioner creado exitosamente");

        }

         */
    }

}
