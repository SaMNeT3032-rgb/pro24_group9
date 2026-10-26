package com.example.security.service;

import com.example.security.dto.common.PageResponse;
import com.example.security.dto.role.RoleRequest;
import com.example.security.dto.role.RoleResponse;
import org.springframework.data.domain.Pageable;

public interface RoleService {

    PageResponse<RoleResponse> findAll(Pageable pageable);

    RoleResponse findById(Long id);

    RoleResponse create(RoleRequest request);

    RoleResponse update(Long id, RoleRequest request);

    void delete(Long id);
}
