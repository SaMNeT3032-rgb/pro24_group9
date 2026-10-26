package com.example.security.dto.user;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(
        Long id,
        String nickName,
        String username,
        boolean enabled,
        boolean accountNonExpired,
        boolean accountNonLocked,
        boolean credentialsNonExpired,
        Set<String> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
