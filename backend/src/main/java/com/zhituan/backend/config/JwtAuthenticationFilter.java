package com.zhituan.backend.config;

import com.zhituan.backend.common.utils.JwtUtils;
import com.zhituan.backend.domain.model.user.AuthSession;
import com.zhituan.backend.repository.user.AuthSessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final AuthSessionRepository authSessionRepository;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, AuthSessionRepository authSessionRepository) {
        this.jwtUtils = jwtUtils;
        this.authSessionRepository = authSessionRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = getJwtFromRequest(request);

        if (StringUtils.hasText(token)) {
            String userId = jwtUtils.parseUserIdFromToken(token);
            if (userId != null) {
                // 双重校验：去数据库检查 Session 是否被串改或已注销
                AuthSession session = authSessionRepository.findByToken(token).orElse(null);
                if (session != null && !session.isExpired(LocalDateTime.now())) {
                    // 构建简单的权限身份
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userId, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
