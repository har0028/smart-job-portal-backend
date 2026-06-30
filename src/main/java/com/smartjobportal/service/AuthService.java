package com.smartjobportal.service;

import com.smartjobportal.dto.request.LoginRequest;
import com.smartjobportal.dto.request.RegisterRequest;
import com.smartjobportal.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
