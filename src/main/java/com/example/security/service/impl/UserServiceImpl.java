package com.example.security.service.impl;

import com.example.security.dto.common.PageResponse;
import com.example.security.dto.user.UserRequest;
import com.example.security.dto.user.UserResponse;
import com.example.security.entity.Role;
import com.example.security.entity.User;
import com.example.security.exception.BadRequestException;
import com.example.security.exception.ConflictException;
import com.example.security.exception.ResourceNotFoundException;
import com.example.security.mapper.UserMapper;
import com.example.security.repository.RoleRepository;
import com.example.security.repository.UserRepository;
import com.example.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> findAll(Pageable pageable) {
        return PageResponse.from(userRepository.findAll(pageable).map(userMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return userMapper.toResponse(getEntity(id));
    }

    @Override
    public UserResponse create(UserRequest request) {
        if (request.password() == null || request.password().isBlank()) {
            throw new BadRequestException("Password is required");
        }
        ensureUnique(request.username(), request.nickName(), null);

        User user = new User();
        applyFields(user, request);
        user.setPassword(passwordEncoder.encode(request.password()));
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse update(Long id, UserRequest request) {
        User user = getEntity(id);
        ensureUnique(request.username(), request.nickName(), id);
        applyFields(user, request);
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        return userMapper.toResponse(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(getEntity(id)); // soft delete via @SQLDelete
    }

    private User getEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private void ensureUnique(String username, String nickName, Long excludeId) {
        boolean usernameTaken = excludeId == null
                ? userRepository.existsByUsername(username)
                : userRepository.existsByUsernameAndIdNot(username, excludeId);
        if (usernameTaken) {
            throw new ConflictException("Username already exists");
        }
        boolean nickTaken = excludeId == null
                ? userRepository.existsByNickName(nickName)
                : userRepository.existsByNickNameAndIdNot(nickName, excludeId);
        if (nickTaken) {
            throw new ConflictException("Nickname already exists");
        }
    }

    private void applyFields(User user, UserRequest request) {
        user.setNickName(request.nickName());
        user.setUsername(request.username());
        if (request.enabled() != null) user.setEnabled(request.enabled());
        if (request.accountNonExpired() != null) user.setAccountNonExpired(request.accountNonExpired());
        if (request.accountNonLocked() != null) user.setAccountNonLocked(request.accountNonLocked());
        if (request.credentialsNonExpired() != null) user.setCredentialsNonExpired(request.credentialsNonExpired());

        if (request.roleIds() != null) {
            Set<Long> ids = new HashSet<>(request.roleIds());
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(ids));
            if (roles.size() != ids.size()) {
                throw new ResourceNotFoundException("One or more roles were not found");
            }
            user.setRoles(roles);
        }
    }
}
