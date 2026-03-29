package com.zhituan.backend.repository.user;

import com.zhituan.backend.domain.model.user.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
}
