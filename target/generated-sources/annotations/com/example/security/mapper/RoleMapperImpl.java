package com.example.security.mapper;

import com.example.security.dto.role.RoleResponse;
import com.example.security.entity.Role;
import java.time.LocalDateTime;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-27T00:54:29+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public RoleResponse toResponse(Role role) {
        if ( role == null ) {
            return null;
        }

        Set<String> permissions = null;
        Long id = null;
        String name = null;
        String description = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        permissions = permissionNames( role.getPermissions() );
        id = role.getId();
        name = role.getName();
        description = role.getDescription();
        createdAt = role.getCreatedAt();
        updatedAt = role.getUpdatedAt();

        RoleResponse roleResponse = new RoleResponse( id, name, description, permissions, createdAt, updatedAt );

        return roleResponse;
    }
}
