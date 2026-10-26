package com.example.security.config;

import com.example.security.entity.Permission;
import com.example.security.entity.Role;
import com.example.security.entity.User;
import com.example.security.repository.PermissionRepository;
import com.example.security.repository.RoleRepository;
import com.example.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/** Seeds permissions, roles and an optional bootstrap admin. Idempotent. */
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.username:}")
    private String adminUsername;

    @Value("${app.bootstrap.admin.password:}")
    private String adminPassword;

    @Value("${app.bootstrap.admin.nick-name:Administrator}")
    private String adminNickName;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Permission userRead = permission("USER_READ", "Read users");
        Permission userWrite = permission("USER_WRITE", "Create, update and delete users");
        Permission roleRead = permission("ROLE_READ", "Read roles");
        Permission roleWrite = permission("ROLE_WRITE", "Create, update and delete roles");
        Permission permissionRead = permission("PERMISSION_READ", "Read permissions");
        Permission permissionWrite = permission("PERMISSION_WRITE", "Create, update and delete permissions");

        Role admin = role("ROLE_ADMIN", "Full access",
                Set.of(userRead, userWrite, roleRead, roleWrite, permissionRead, permissionWrite));
        role("ROLE_MANAGER", "Manages users and reads roles", Set.of(userRead, userWrite, roleRead));
        role("ROLE_USER", "Default role for new users", Set.of());

        if (!adminUsername.isBlank() && !adminPassword.isBlank()
                && !userRepository.existsByUsername(adminUsername)) {
            User admin1 = new User();
            admin1.setNickName(adminNickName);
            admin1.setUsername(adminUsername);
            admin1.setPassword(passwordEncoder.encode(adminPassword));
            admin1.getRoles().add(admin);
            userRepository.save(admin1);
        }
    }

    private Permission permission(String name, String description) {
        return permissionRepository.findByName(name).orElseGet(() -> {
            Permission p = new Permission();
            p.setName(name);
            p.setDescription(description);
            return permissionRepository.save(p);
        });
    }

    private Role role(String name, String description, Set<Permission> permissions) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role r = new Role();
            r.setName(name);
            r.setDescription(description);
            r.getPermissions().addAll(permissions);
            return roleRepository.save(r);
        });
    }
}
