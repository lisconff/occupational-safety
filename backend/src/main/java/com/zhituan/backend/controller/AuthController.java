package com.zhituan.backend.controller;

import com.zhituan.backend.common.api.ApiResponse;
import com.zhituan.backend.dto.AuthDtos;
import com.zhituan.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthDtos.AuthResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        return ApiResponse.ok("注册成功", authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return ApiResponse.ok("登录成功", authService.login(request));
    }
}
