package com.example.security.service;

import com.example.security.dto.auth.AuthResponse;
import com.example.security.dto.auth.LoginRequest;
import com.example.security.dto.auth.RefreshRequest;
import com.example.security.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshRequest request);

    void logout(String accessToken, String refreshToken);
}
