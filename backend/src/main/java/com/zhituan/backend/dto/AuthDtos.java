package com.zhituan.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public record RegisterRequest(@NotBlank String username, @NotBlank String password) {
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    public record AuthResponse(String userId, String username, String role, String token) {
    }
}
