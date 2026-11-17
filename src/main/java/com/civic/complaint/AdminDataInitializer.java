
package com.civic.complaint;

import com.civic.complaint.entity.Admin;
import com.civic.complaint.repository.AdminRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminDataInitializer {

    @Bean
    public CommandLineRunner createAdmin(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.email}") String email,
            @Value("${app.admin.password}") String password) {

        return args -> {

            if (adminRepository.findByEmail(email).isEmpty()) {

                Admin admin = new Admin();

                admin.setName("System Administrator");
                admin.setEmail(email);
                admin.setPassword(passwordEncoder.encode(password));

                adminRepository.save(admin);

                System.out.println("Admin account created successfully!");
            }
        };
    }
}
