package com.example.security.service;

import com.example.security.dto.common.PageResponse;
import com.example.security.dto.permission.PermissionRequest;
import com.example.security.dto.permission.PermissionResponse;
import org.springframework.data.domain.Pageable;

public interface PermissionService {

    PageResponse<PermissionResponse> findAll(Pageable pageable);

    PermissionResponse findById(Long id);

    PermissionResponse create(PermissionRequest request);

    PermissionResponse update(Long id, PermissionRequest request);

    void delete(Long id);
}
