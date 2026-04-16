package com.zhituan.backend.service;

import com.zhituan.backend.domain.model.user.UserProfile;
import com.zhituan.backend.dto.UserDtos;

public interface UserService {
    UserProfile getProfile(String userId);

    UserProfile updateProfile(String userId, UserDtos.UpdateProfileRequest request);

    UserProfile updateAvatar(String userId, UserDtos.UpdateAvatarRequest request);

    UserProfile updateActivity(String userId, UserDtos.ActivityUpdateRequest request);

    UserDtos.RiskAssessmentView getRiskAssessment(String userId);
}
