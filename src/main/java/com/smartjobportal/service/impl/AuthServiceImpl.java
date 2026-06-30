package com.smartjobportal.service.impl;

import com.smartjobportal.dto.request.LoginRequest;
import com.smartjobportal.dto.request.RegisterRequest;
import com.smartjobportal.dto.response.AuthResponse;
import com.smartjobportal.entity.RecruiterProfile;
import com.smartjobportal.entity.SeekerProfile;
import com.smartjobportal.entity.User;
import com.smartjobportal.enums.Role;
import com.smartjobportal.exception.BadRequestException;
import com.smartjobportal.repository.RecruiterProfileRepository;
import com.smartjobportal.repository.SeekerProfileRepository;
import com.smartjobportal.repository.UserRepository;
import com.smartjobportal.security.CustomUserDetails;
import com.smartjobportal.security.JwtService;
import com.smartjobportal.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final SeekerProfileRepository seekerProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered: " + request.getEmail());
        }

        if (request.getRole() == Role.ADMIN) {
            throw new BadRequestException("Cannot self-register as ADMIN");
        }

        if (request.getRole() == Role.RECRUITER) {
            if (request.getCompanyName() == null || request.getCompanyName().isBlank()) {
                throw new BadRequestException("Company name is required for recruiter registration");
            }
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isActive(true)
                .build();

        user = userRepository.save(user);

        if (request.getRole() == Role.JOB_SEEKER) {
            SeekerProfile profile = SeekerProfile.builder()
                    .user(user)
                    .yearsExperience(0)
                    .build();
            seekerProfileRepository.save(profile);
        } else if (request.getRole() == Role.RECRUITER) {
            RecruiterProfile profile = RecruiterProfile.builder()
                    .user(user)
                    .companyName(request.getCompanyName())
                    .companyWebsite(request.getCompanyWebsite())
                    .designation(request.getDesignation())
                    .build();
            recruiterProfileRepository.save(profile);
        }

        log.info("User registered successfully: {} with role {}", user.getEmail(), user.getRole());

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);

        log.info("User logged in: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
