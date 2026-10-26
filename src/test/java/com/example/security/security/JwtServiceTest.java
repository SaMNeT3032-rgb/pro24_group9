package com.example.security.security;

import com.example.security.entity.Permission;
import com.example.security.entity.Role;
import com.example.security.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService(
            "test-secret-key-that-is-at-least-32-bytes!!", 900_000L, 604_800_000L);

    @Test
    void accessTokenContainsSubjectTypeRolesAndPermissions() {
        Claims claims = jwtService.parseClaims(jwtService.generateAccessToken(principal()));

        assertEquals("alice", claims.getSubject());
        assertEquals(JwtService.ACCESS, claims.get(JwtService.TOKEN_TYPE_CLAIM, String.class));
        assertTrue(claims.get("roles", java.util.List.class).contains("ROLE_ADMIN"));
        assertTrue(claims.get("permissions", java.util.List.class).contains("USER_READ"));
    }

    @Test
    void refreshTokenHasRefreshType() {
        Claims claims = jwtService.parseClaims(jwtService.generateRefreshToken(principal()));

        assertEquals(JwtService.REFRESH, claims.get(JwtService.TOKEN_TYPE_CLAIM, String.class));
    }

    @Test
    void tamperedTokenIsRejected() {
        String token = jwtService.generateAccessToken(principal());

        assertThrows(JwtException.class, () -> jwtService.parseClaims(token + "x"));
    }

    private UserPrincipal principal() {
        Permission read = new Permission();
        read.setName("USER_READ");
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        role.getPermissions().add(read);
        User user = new User();
        user.setUsername("alice");
        user.setPassword("encoded");
        user.getRoles().add(role);
        return new UserPrincipal(user);
    }
}
