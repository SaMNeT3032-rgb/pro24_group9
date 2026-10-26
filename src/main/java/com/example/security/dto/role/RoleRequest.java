package com.example.security.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record RoleRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 255) String description,
        Set<Long> permissionIds) {
}
