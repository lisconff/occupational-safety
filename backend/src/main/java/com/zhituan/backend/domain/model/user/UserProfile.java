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

    @Column(length = 2000)
    private String bio;
}
