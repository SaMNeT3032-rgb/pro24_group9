package com.example.security.mapper;

import com.example.security.dto.permission.PermissionResponse;
import com.example.security.entity.Permission;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-27T00:54:29+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class PermissionMapperImpl implements PermissionMapper {

    @Override
    public PermissionResponse toResponse(Permission permission) {
        if ( permission == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        String description = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = permission.getId();
        name = permission.getName();
        description = permission.getDescription();
        createdAt = permission.getCreatedAt();
        updatedAt = permission.getUpdatedAt();

        PermissionResponse permissionResponse = new PermissionResponse( id, name, description, createdAt, updatedAt );

        return permissionResponse;
    }
}
