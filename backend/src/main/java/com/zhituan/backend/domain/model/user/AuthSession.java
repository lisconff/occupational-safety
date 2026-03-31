package com.zhituan.backend.domain.model.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSession {

    @Id
    @Column(length = 500)
    private String token;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private String deviceInfo;

    // --- 领域行为方法 (Rich Domain Model) ---

    /**
     * 判断会话是否过期
     */
    public boolean isExpired(LocalDateTime currentTime) {
        if (currentTime == null) {
            currentTime = LocalDateTime.now();
        }
        return expiresAt != null && currentTime.isAfter(expiresAt);
    }

    /**
     * 刷新会话过期时间
     */
    public void refresh(LocalDateTime newExpiresAt) {
        this.expiresAt = newExpiresAt;
    }

    /**
     * 立即注销该会话
     */
    public void revoke() {
        this.expiresAt = LocalDateTime.now().minusSeconds(1); // 将过期时间调至当前时间之前
    }
}
