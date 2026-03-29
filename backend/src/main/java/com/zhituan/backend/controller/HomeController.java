package com.zhituan.backend.controller;

import com.zhituan.backend.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping({"/", "/api"})
    public ApiResponse<Map<String, String>> home() {
        return ApiResponse.ok(Map.of(
                "name", "occupational-safety-backend",
                "status", "UP",
                "health", "/api/health"
        ));
    }
}
