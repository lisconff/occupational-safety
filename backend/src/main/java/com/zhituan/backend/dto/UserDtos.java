package com.zhituan.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class UserDtos {

    public record UpdateProfileRequest(
            @NotBlank String school,
            @NotBlank String major,
            Integer graduationYear,
            String expectedCity,
            String expectedSalary,
            String bio
    ) {
    }

        public record ActivityUpdateRequest(
            Long onlineSeconds,
            Integer aiQueryCount,
            Integer caseStudyCount,
            Integer simulationCount,
            Integer forumViewCount
        ) {
        }

        public record RiskAssessmentView(
            long onlineSeconds,
            int aiQueryCount,
            int caseStudyCount,
            int simulationCount,
            int forumViewCount,
            int onlineScore,
            int aiScore,
            int caseScore,
            int simulationScore,
            int forumScore,
            int antiFraudValue
        ) {
        }

        public record UpdateAvatarRequest(
            String avatarDataUrl
        ) {
        }
}
