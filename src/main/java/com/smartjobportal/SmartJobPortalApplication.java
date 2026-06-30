package com.smartjobportal;

import com.smartjobportal.entity.User;
import com.smartjobportal.enums.Role;
import com.smartjobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class SmartJobPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartJobPortalApplication.class, args);
    }

    @Bean
    CommandLineRunner seedAdminUser(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.email}") String adminEmail,
            @Value("${app.admin.password}") String adminPassword,
            @Value("${app.admin.name}") String adminName
    ) {
        return args -> {
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = User.builder()
                        .fullName(adminName)
                        .email(adminEmail)
                        .passwordHash(passwordEncoder.encode(adminPassword))
                        .role(Role.ADMIN)
                        .isActive(true)
                        .build();
                userRepository.save(admin);
                log.info("==============================================");
                log.info("Admin user seeded: {}", adminEmail);
                log.info("==============================================");
            } else {
                log.info("Admin user already exists: {}", adminEmail);
            }
        };
    }
}
