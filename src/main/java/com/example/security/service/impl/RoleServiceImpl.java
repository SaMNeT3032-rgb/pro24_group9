package com.example.security.service.impl;

import com.example.security.dto.common.PageResponse;
import com.example.security.dto.role.RoleRequest;
import com.example.security.dto.role.RoleResponse;
import com.example.security.entity.Permission;
import com.example.security.entity.Role;
import com.example.security.exception.ConflictException;
import com.example.security.exception.ResourceNotFoundException;
import com.example.security.mapper.RoleMapper;
import com.example.security.repository.PermissionRepository;
import com.example.security.repository.RoleRepository;
import com.example.security.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RoleResponse> findAll(Pageable pageable) {
        return PageResponse.from(roleRepository.findAll(pageable).map(roleMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse findById(Long id) {
        return roleMapper.toResponse(getEntity(id));
    }

    @Override
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByName(request.name())) {
            throw new ConflictException("Role name already exists");
        }
        Role role = new Role();
        role.setName(request.name());
        role.setDescription(request.description());
        applyPermissions(role, request.permissionIds());
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    public RoleResponse update(Long id, RoleRequest request) {
        Role role = getEntity(id);
        if (roleRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new ConflictException("Role name already exists");
        }
        role.setName(request.name());
        role.setDescription(request.description());
        applyPermissions(role, request.permissionIds());
        return roleMapper.toResponse(role);
    }

    @Override
    public void delete(Long id) {
        roleRepository.delete(getEntity(id));
    }

    private Role getEntity(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + id));
    }

    private void applyPermissions(Role role, Set<Long> permissionIds) {
        if (permissionIds == null) {
            return; // null means "leave unchanged"
        }
        Set<Long> ids = new HashSet<>(permissionIds);
        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(ids));
        if (permissions.size() != ids.size()) {
            throw new ResourceNotFoundException("One or more permissions were not found");
        }
        role.setPermissions(permissions);
    }
}
