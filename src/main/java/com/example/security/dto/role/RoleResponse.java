package com.example.security.dto.role;

import java.time.LocalDateTime;
import java.util.Set;

public record RoleResponse(
        Long id,
        String name,
        String description,
        Set<String> permissions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
