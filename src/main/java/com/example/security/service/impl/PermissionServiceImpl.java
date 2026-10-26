package com.example.security.service.impl;

import com.example.security.dto.common.PageResponse;
import com.example.security.dto.permission.PermissionRequest;
import com.example.security.dto.permission.PermissionResponse;
import com.example.security.entity.Permission;
import com.example.security.exception.ConflictException;
import com.example.security.exception.ResourceNotFoundException;
import com.example.security.mapper.PermissionMapper;
import com.example.security.repository.PermissionRepository;
import com.example.security.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PermissionResponse> findAll(Pageable pageable) {
        return PageResponse.from(permissionRepository.findAll(pageable).map(permissionMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse findById(Long id) {
        return permissionMapper.toResponse(getEntity(id));
    }

    @Override
    public PermissionResponse create(PermissionRequest request) {
        if (permissionRepository.existsByName(request.name())) {
            throw new ConflictException("Permission name already exists");
        }
        Permission permission = new Permission();
        permission.setName(request.name());
        permission.setDescription(request.description());
        return permissionMapper.toResponse(permissionRepository.save(permission));
    }

    @Override
    public PermissionResponse update(Long id, PermissionRequest request) {
        Permission permission = getEntity(id);
        if (permissionRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new ConflictException("Permission name already exists");
        }
        permission.setName(request.name());
        permission.setDescription(request.description());
        return permissionMapper.toResponse(permission);
    }

    @Override
    public void delete(Long id) {
        permissionRepository.delete(getEntity(id));
    }

    private Permission getEntity(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found: " + id));
    }
}
