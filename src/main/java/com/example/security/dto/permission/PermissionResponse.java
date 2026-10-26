package com.example.security.dto.permission;

import java.time.LocalDateTime;

public record PermissionResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
