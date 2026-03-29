package com.zhituan.backend.service.impl;

import com.zhituan.backend.domain.model.user.User;
import com.zhituan.backend.dto.AuthDtos;
import com.zhituan.backend.repository.user.UserRepository;
import com.zhituan.backend.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(u -> {
            throw new IllegalArgumentException("用户名已存在");
        });

        User user = User.builder()
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role("USER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);
        return new AuthDtos.AuthResponse(saved.getUserId(), saved.getUsername(), saved.getRole(), issueToken());
    }

    @Override
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        return new AuthDtos.AuthResponse(user.getUserId(), user.getUsername(), user.getRole(), issueToken());
    }

    private String issueToken() {
        return "dev-token-" + UUID.randomUUID();
    }
}
