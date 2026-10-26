package com.example.security.service.impl;

import com.example.security.dto.auth.AuthResponse;
import com.example.security.dto.auth.LoginRequest;
import com.example.security.dto.auth.RefreshRequest;
import com.example.security.dto.auth.RegisterRequest;
import com.example.security.entity.RevokedToken;
import com.example.security.entity.Role;
import com.example.security.entity.User;
import com.example.security.exception.ConflictException;
import com.example.security.repository.RevokedTokenRepository;
import com.example.security.repository.RoleRepository;
import com.example.security.repository.UserRepository;
import com.example.security.security.JwtService;
import com.example.security.security.UserPrincipal;
import com.example.security.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_ROLE = "ROLE_USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RevokedTokenRepository revokedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already exists");
        }
        if (userRepository.existsByNickName(request.nickName())) {
            throw new ConflictException("Nickname already exists");
        }

        User user = new User();
        user.setNickName(request.nickName());
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));

        Role defaultRole = roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new IllegalStateException(DEFAULT_ROLE + " is missing"));
        user.getRoles().add(defaultRole);

        userRepository.save(user);
        return issueTokens(new UserPrincipal(user));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        return issueTokens((UserPrincipal) authentication.getPrincipal());
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) {
        Claims claims = parseTokenOfType(request.refreshToken(), JwtService.REFRESH);
        if (revokedTokenRepository.existsById(claims.getId())) {
            throw new BadCredentialsException("Refresh token has been revoked");
        }

        User user = userRepository.findByUsername(claims.getSubject())
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        UserPrincipal principal = new UserPrincipal(user);
        if (!principal.isEnabled() || !principal.isAccountNonLocked()) {
            throw new BadCredentialsException("Account is not active");
        }

        // Rotation: the used refresh token can never be used again.
        revoke(claims);
        return issueTokens(principal);
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        revoke(parseTokenOfType(accessToken, JwtService.ACCESS));
        if (refreshToken != null && !refreshToken.isBlank()) {
            revoke(parseTokenOfType(refreshToken, JwtService.REFRESH));
        }
    }

    private AuthResponse issueTokens(UserPrincipal principal) {
        return new AuthResponse(
                jwtService.generateAccessToken(principal),
                jwtService.generateRefreshToken(principal),
                "Bearer");
    }

    private Claims parseTokenOfType(String token, String expectedType) {
        try {
            Claims claims = jwtService.parseClaims(token);
            if (!expectedType.equals(claims.get(JwtService.TOKEN_TYPE_CLAIM, String.class))) {
                throw new BadCredentialsException("Invalid " + expectedType + " token");
            }
            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            throw new BadCredentialsException("Invalid " + expectedType + " token");
        }
    }

    private void revoke(Claims claims) {
        LocalDateTime expiresAt = LocalDateTime.ofInstant(
                claims.getExpiration().toInstant(), ZoneId.systemDefault());
        revokedTokenRepository.save(new RevokedToken(claims.getId(), expiresAt));
    }
}
