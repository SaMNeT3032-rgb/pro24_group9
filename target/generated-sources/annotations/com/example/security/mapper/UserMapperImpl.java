package com.example.security.mapper;

import com.example.security.dto.user.UserResponse;
import com.example.security.entity.User;
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
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        Set<String> roles = null;
        Long id = null;
        String nickName = null;
        String username = null;
        boolean enabled = false;
        boolean accountNonExpired = false;
        boolean accountNonLocked = false;
        boolean credentialsNonExpired = false;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        roles = roleNames( user.getRoles() );
        id = user.getId();
        nickName = user.getNickName();
        username = user.getUsername();
        enabled = user.isEnabled();
        accountNonExpired = user.isAccountNonExpired();
        accountNonLocked = user.isAccountNonLocked();
        credentialsNonExpired = user.isCredentialsNonExpired();
        createdAt = user.getCreatedAt();
        updatedAt = user.getUpdatedAt();

        UserResponse userResponse = new UserResponse( id, nickName, username, enabled, accountNonExpired, accountNonLocked, credentialsNonExpired, roles, createdAt, updatedAt );

        return userResponse;
    }
}
