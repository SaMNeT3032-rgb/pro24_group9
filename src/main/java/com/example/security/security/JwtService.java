package com.example.security.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    public static final String TOKEN_TYPE_CLAIM = "type";
    public static final String ACCESS = "access";
    public static final String REFRESH = "refresh";

    private final SecretKey key;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.access-token-expiration}") long accessExpiration,
                      @Value("${app.jwt.refresh-token-expiration}") long refreshExpiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String generateAccessToken(UserPrincipal principal) {
        return buildToken(principal.getUsername(), ACCESS, accessExpiration,
                Map.of("roles", principal.getRoleNames(),
                       "permissions", principal.getPermissionNames()));
    }

    public String generateRefreshToken(UserPrincipal principal) {
        return buildToken(principal.getUsername(), REFRESH, refreshExpiration, Map.of());
    }

    private String buildToken(String subject, String type, long ttl, Map<String, Object> extraClaims) {
        Date now = new Date();
        return Jwts.builder()
                .subject(subject)
                .claims(extraClaims)
                .claim(TOKEN_TYPE_CLAIM, type)
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttl))
                .signWith(key)
                .compact();
    }

    /** Verifies signature and expiration. Throws JwtException when invalid. */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
