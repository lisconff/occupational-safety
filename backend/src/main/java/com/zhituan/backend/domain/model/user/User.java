package com.zhituan.backend.domain.model.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String role;

    private String avatarImageUrl;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- 领域行为方法 (Rich Domain Model) ---

    /**
     * 更新用户基本信息
     */
    public void updateBaseInfo(String username, String avatarImageUrl, String email, String phone) {
        this.username = username;
        this.avatarImageUrl = avatarImageUrl;
        this.email = email;
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 变更用户角色
     */
    public void changeRole(String role) {
        this.role = role;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 验证密码是否匹配
     */
    public boolean verifyPassword(String rawPassword, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        if (passwordEncoder == null || rawPassword == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, this.passwordHash);
    }

    /**
     * 重置密码
     */
    public void resetPassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.updatedAt = LocalDateTime.now();
    }
}
