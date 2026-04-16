package com.zhituan.backend.service.impl;

import com.zhituan.backend.domain.model.user.UserProfile;
import com.zhituan.backend.domain.model.user.User;
import com.zhituan.backend.dto.UserDtos;
import com.zhituan.backend.repository.user.UserProfileRepository;
import com.zhituan.backend.repository.user.UserRepository;
import com.zhituan.backend.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    public UserServiceImpl(UserProfileRepository userProfileRepository, UserRepository userRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserProfile getProfile(String userId) {
        UserProfile profile = userProfileRepository.findById(userId).orElse(null);
        if (profile != null) {
            return profile;
        }

        User user = userRepository.findByUsername(userId).orElse(null);
        if (user != null) {
            return userProfileRepository.findById(user.getUserId())
                    .orElse(UserProfile.builder().userId(user.getUserId()).build());
        }

        return UserProfile.builder().userId(userId).build();
    }

    @Override
    public UserProfile updateProfile(String userId, UserDtos.UpdateProfileRequest request) {
        UserProfile profile = userProfileRepository.findById(userId).orElse(UserProfile.builder().userId(userId).build());
        profile.setSchool(request.school());
        profile.setMajor(request.major());
        profile.setGraduationYear(request.graduationYear());
        profile.setExpectedCity(request.expectedCity());
        profile.setExpectedSalary(request.expectedSalary());
        profile.setBio(request.bio());
        return userProfileRepository.save(profile);
    }

    @Override
    public UserProfile updateAvatar(String userId, UserDtos.UpdateAvatarRequest request) {
        UserProfile profile = userProfileRepository.findById(userId).orElse(UserProfile.builder().userId(userId).build());
        String avatarDataUrl = request == null ? null : request.avatarDataUrl();
        if (avatarDataUrl != null && avatarDataUrl.length() > 8_000_000) {
            throw new IllegalArgumentException("头像数据过大，请压缩后重试");
        }
        profile.setAvatarDataUrl(avatarDataUrl);
        return userProfileRepository.save(profile);
    }
}
