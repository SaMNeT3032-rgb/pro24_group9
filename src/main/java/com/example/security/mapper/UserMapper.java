package com.example.security.mapper;

import com.example.security.dto.user.UserResponse;
import com.example.security.entity.Role;
import com.example.security.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "roleNames")
    UserResponse toResponse(User user);

    @Named("roleNames")
    default Set<String> roleNames(Set<Role> roles) {
        return roles.stream().map(Role::getName).collect(Collectors.toSet());
    }
}
