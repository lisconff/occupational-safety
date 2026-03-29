package com.zhituan.backend.service;

import com.zhituan.backend.dto.AuthDtos;

public interface AuthService {
    AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request);

    AuthDtos.AuthResponse login(AuthDtos.LoginRequest request);
}
