package com.example.security.service;

import com.example.security.dto.auth.AuthResponse;
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
import com.example.security.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private RevokedTokenRepository revokedTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;

    private JwtService jwtService;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("test-secret-key-that-is-at-least-32-bytes!!", 900_000L, 604_800_000L);
        authService = new AuthServiceImpl(userRepository, roleRepository, revokedTokenRepository,
                passwordEncoder, authenticationManager, jwtService);
    }

    @Test
    void register_throwsConflict_whenUsernameExists() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> authService.register(new RegisterRequest("Alice", "alice", "password123")));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_hashesPasswordAssignsDefaultRoleAndReturnsTokens() {
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByNickName("Alice")).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        AuthResponse response = authService.register(new RegisterRequest("Alice", "alice", "password123"));

        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        verify(passwordEncoder).encode("password123");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("hashed", captor.getValue().getPassword());
        assertTrue(captor.getValue().getRoles().contains(userRole));
    }

    @Test
    void refresh_rejectsAccessToken() {
        String accessToken = jwtService.generateAccessToken(principal());

        assertThrows(BadCredentialsException.class,
                () -> authService.refresh(new RefreshRequest(accessToken)));
    }

    @Test
    void logout_revokesAccessToken() {
        String accessToken = jwtService.generateAccessToken(principal());

        authService.logout(accessToken, null);

        verify(revokedTokenRepository).save(any(RevokedToken.class));
    }

    private UserPrincipal principal() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword("encoded");
        return new UserPrincipal(user);
    }
}
