package com.zhituan.backend.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 权限与安全上下文工具类
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户的ID
     * （需要在后续JWT拦截器中将userId存入Principal）
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 判断当前是否已登录
     */
    public static boolean isAuthenticated() {
        return getCurrentUserId() != null;
    }
}
