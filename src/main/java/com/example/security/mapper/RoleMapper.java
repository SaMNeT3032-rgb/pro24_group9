package com.example.security.mapper;

import com.example.security.dto.role.RoleResponse;
import com.example.security.entity.Permission;
import com.example.security.entity.Role;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "permissionNames")
    RoleResponse toResponse(Role role);

    @Named("permissionNames")
    default Set<String> permissionNames(Set<Permission> permissions) {
        return permissions.stream().map(Permission::getName).collect(Collectors.toSet());
    }
}
