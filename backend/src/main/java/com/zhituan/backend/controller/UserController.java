package com.zhituan.backend.controller;

import com.zhituan.backend.common.api.ApiResponse;
import com.zhituan.backend.domain.model.user.UserProfile;
import com.zhituan.backend.dto.UserDtos;
import com.zhituan.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}/profile")
    public ApiResponse<UserProfile> getProfile(@PathVariable String userId) {
        return ApiResponse.ok(userService.getProfile(userId));
    }

    @PutMapping("/{userId}/profile")
    public ApiResponse<UserProfile> updateProfile(
            @PathVariable String userId,
            @Valid @RequestBody UserDtos.UpdateProfileRequest request
    ) {
        return ApiResponse.ok("更新成功", userService.updateProfile(userId, request));
    }
}
