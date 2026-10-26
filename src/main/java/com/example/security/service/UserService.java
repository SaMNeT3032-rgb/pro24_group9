package com.example.security.service;

import com.example.security.dto.common.PageResponse;
import com.example.security.dto.user.UserRequest;
import com.example.security.dto.user.UserResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {

    PageResponse<UserResponse> findAll(Pageable pageable);

    UserResponse findById(Long id);

    UserResponse create(UserRequest request);

    UserResponse update(Long id, UserRequest request);

    void delete(Long id);
}
