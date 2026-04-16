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

    @Override
    public UserProfile updateActivity(String userId, UserDtos.ActivityUpdateRequest request) {
        UserProfile profile = userProfileRepository.findById(userId).orElse(UserProfile.builder().userId(userId).build());
        profile.setOnlineSeconds(safeLong(profile.getOnlineSeconds()) + safeLong(request == null ? null : request.onlineSeconds()));
        profile.setAiQueryCount(safeInt(profile.getAiQueryCount()) + safeInt(request == null ? null : request.aiQueryCount()));
        profile.setCaseStudyCount(safeInt(profile.getCaseStudyCount()) + safeInt(request == null ? null : request.caseStudyCount()));
        profile.setSimulationCount(safeInt(profile.getSimulationCount()) + safeInt(request == null ? null : request.simulationCount()));
        profile.setForumViewCount(safeInt(profile.getForumViewCount()) + safeInt(request == null ? null : request.forumViewCount()));
        return userProfileRepository.save(profile);
    }

    @Override
    public UserDtos.RiskAssessmentView getRiskAssessment(String userId) {
        UserProfile profile = userProfileRepository.findById(userId).orElse(UserProfile.builder().userId(userId).build());

        long onlineSeconds = safeLong(profile.getOnlineSeconds());
        int aiCount = safeInt(profile.getAiQueryCount());
        int caseCount = safeInt(profile.getCaseStudyCount());
        int simulationCount = safeInt(profile.getSimulationCount());
        int forumCount = safeInt(profile.getForumViewCount());

        int onlineScore = toDimensionScore(onlineSeconds / 60.0, 600.0);
        int aiScore = toDimensionScore(aiCount, 220.0);
        int caseScore = toDimensionScore(caseCount, 80.0);
        int simulationScore = toDimensionScore(simulationCount, 60.0);
        int forumScore = toDimensionScore(forumCount, 180.0);

        double weighted = onlineScore * 0.24 + aiScore * 0.24 + caseScore * 0.20 + simulationScore * 0.20 + forumScore * 0.12;
        int antiFraudValue = 60 + (int) Math.round(40 * Math.pow(Math.max(0.0, Math.min(1.0, weighted / 100.0)), 1.85));

        return new UserDtos.RiskAssessmentView(
                onlineSeconds,
                aiCount,
                caseCount,
                simulationCount,
                forumCount,
                onlineScore,
                aiScore,
                caseScore,
                simulationScore,
                forumScore,
                Math.max(60, Math.min(100, antiFraudValue))
        );
    }

    private int toDimensionScore(double value, double maxForFull) {
        if (value <= 0) return 0;
        double ratio = Math.log1p(value) / Math.log1p(maxForFull);
        return (int) Math.round(Math.max(0.0, Math.min(1.0, ratio)) * 100);
    }

    private int safeInt(Integer value) {
        return Math.max(0, value == null ? 0 : value);
    }

    private long safeLong(Long value) {
        return Math.max(0L, value == null ? 0L : value);
    }
}
