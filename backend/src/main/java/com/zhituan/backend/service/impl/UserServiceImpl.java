package com.zhituan.backend.service.impl;

import com.zhituan.backend.domain.model.user.UserProfile;
import com.zhituan.backend.dto.UserDtos;
import com.zhituan.backend.repository.user.UserProfileRepository;
import com.zhituan.backend.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserProfileRepository userProfileRepository;

    public UserServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public UserProfile getProfile(String userId) {
        return userProfileRepository.findById(userId)
                .orElse(UserProfile.builder().userId(userId).build());
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
}
