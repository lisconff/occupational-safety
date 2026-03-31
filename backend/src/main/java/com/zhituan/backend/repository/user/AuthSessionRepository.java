package com.zhituan.backend.repository.user;

import com.zhituan.backend.domain.model.user.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthSessionRepository extends JpaRepository<AuthSession, String> {
    Optional<AuthSession> findByToken(String token);
}
