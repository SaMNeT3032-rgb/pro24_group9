package com.example.security.dto.permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PermissionRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 255) String description) {
}
