package com.example.security.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Used for create and update. On create, password is required.
 * On update, null fields are left unchanged.
 */
public record UserRequest(
        @NotBlank @Size(max = 100) String nickName,
        @NotBlank @Size(min = 3, max = 100) String username,
        @Size(min = 8, max = 72) String password,
        Boolean enabled,
        Boolean accountNonExpired,
        Boolean accountNonLocked,
        Boolean credentialsNonExpired,
        Set<Long> roleIds) {
}
