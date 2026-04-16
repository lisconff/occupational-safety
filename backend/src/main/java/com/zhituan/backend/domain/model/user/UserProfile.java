package com.zhituan.backend.domain.model.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    private String userId;

    private String school;
    private String major;
    private Integer graduationYear;
    private String expectedCity;
    private String expectedSalary;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Lob
    @Column(name = "avatar_data_url", columnDefinition = "LONGTEXT")
    private String avatarDataUrl;

    // --- 领域行为方法 (Rich Domain Model) ---

    /**
     * 更新用户档案
     */
    public void updateProfile(String school, String major, Integer graduationYear, String expectedCity, String expectedSalary, String bio) {
        this.school = school;
        this.major = major;
        this.graduationYear = graduationYear;
        this.expectedCity = expectedCity;
        this.expectedSalary = expectedSalary;
        this.bio = bio;
    }

    /**
     * 校验资料完整度 (核心字段是否已填)
     */
    public boolean validateProfileCompleteness() {
        return school != null && !school.trim().isEmpty() &&
               major != null && !major.trim().isEmpty() &&
               graduationYear != null;
    }

    /**
     * 导出简历摘要模板
     */
    public String toResumeSummary() {
        String base = (school != null ? school : "未知学校") + " - " + (major != null ? major : "未知专业");
        if (graduationYear != null) {
            base += " (" + graduationYear + "届)";
        }
        return base;
    }
}
