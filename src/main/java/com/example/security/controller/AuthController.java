package com.example.security.controller;

import com.example.security.dto.auth.AuthResponse;
import com.example.security.dto.auth.LoginRequest;
import com.example.security.dto.auth.LogoutRequest;
import com.example.security.dto.auth.RefreshRequest;
import com.example.security.dto.auth.RegisterRequest;
import com.example.security.security.UserPrincipal;
import com.example.security.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                       @RequestBody(required = false) LogoutRequest request) {
        String accessToken = authorization.replaceFirst("^Bearer ", "");
        authService.logout(accessToken, request == null ? null : request.refreshToken());
    }

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal UserPrincipal principal) {
        return Map.of(
                "id", principal.getId(),
                "username", principal.getUsername(),
                "roles", principal.getRoleNames(),
                "permissions", principal.getPermissionNames());
    }
}
