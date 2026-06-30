package com.smartjobportal.controller;

import com.smartjobportal.dto.request.LoginRequest;
import com.smartjobportal.dto.request.RegisterRequest;
import com.smartjobportal.dto.response.ApiResponse;
import com.smartjobportal.dto.response.AuthResponse;
import com.smartjobportal.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Body: { fullName, email, password, role, companyName (recruiter only) }
     * Response: 201 + AuthResponse with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    /**
     * POST /api/auth/login
     * Body: { email, password }
     * Response: 200 + AuthResponse with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}
