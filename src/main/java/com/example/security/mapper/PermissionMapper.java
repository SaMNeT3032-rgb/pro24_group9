package com.example.security.mapper;

import com.example.security.dto.permission.PermissionResponse;
import com.example.security.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionResponse toResponse(Permission permission);
}
