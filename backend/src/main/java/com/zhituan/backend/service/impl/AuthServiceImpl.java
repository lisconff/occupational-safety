package com.zhituan.backend.service.impl;

import com.zhituan.backend.common.exception.BusinessException;
import com.zhituan.backend.common.utils.JwtUtils;
import com.zhituan.backend.domain.model.user.AuthSession;
import com.zhituan.backend.domain.model.user.User;
import com.zhituan.backend.dto.AuthDtos;
import com.zhituan.backend.repository.user.AuthSessionRepository;
import com.zhituan.backend.repository.user.UserRepository;
import com.zhituan.backend.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.time.temporal.ChronoUnit;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthSessionRepository authSessionRepository;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, AuthSessionRepository authSessionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authSessionRepository = authSessionRepository;
    }

    @Override
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(u -> {
            throw new BusinessException(400, "用户名已存在请更换");
        });

        User user = User.builder()
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role("USER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);
        return new AuthDtos.AuthResponse(saved.getUserId(), saved.getUsername(), saved.getRole(), issueToken(saved.getUserId()));
    }

    @Override
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));

        if (!user.verifyPassword(request.password(), passwordEncoder)) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        return new AuthDtos.AuthResponse(user.getUserId(), user.getUsername(), user.getRole(), issueToken(user.getUserId()));
    }

    private String issueToken(String userId) {
        // 1. 生成真实的 JWT Token
        String token = jwtUtils.generateToken(userId);
        
        // 2. 将 Token 存入我们建好的 AuthSession 表
        AuthSession session = AuthSession.builder()
                .token(token)
                .userId(userId)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plus(jwtUtils.getExpirationTime(), ChronoUnit.MILLIS))
                .deviceInfo("Web Browser")
                .build();
        authSessionRepository.save(session);
        
        return token;
    }
}

