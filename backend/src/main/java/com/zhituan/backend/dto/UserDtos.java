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
}
