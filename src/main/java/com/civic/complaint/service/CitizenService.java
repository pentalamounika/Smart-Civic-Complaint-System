
package com.civic.complaint.service;

import com.civic.complaint.entity.Citizen;
import com.civic.complaint.repository.CitizenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CitizenService {

    private final CitizenRepository citizenRepository;
    private final PasswordEncoder passwordEncoder;

    public CitizenService(CitizenRepository citizenRepository,
                          PasswordEncoder passwordEncoder) {
        this.citizenRepository = citizenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerCitizen(Citizen citizen, String confirmPassword) {

        if (!citizen.getPassword().equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (citizenRepository.existsByEmail(citizen.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        citizen.setPassword(
            passwordEncoder.encode(citizen.getPassword())
        );

        citizenRepository.save(citizen);
    }
}
