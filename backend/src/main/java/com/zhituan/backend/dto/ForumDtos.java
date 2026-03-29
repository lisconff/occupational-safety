package com.zhituan.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class ForumDtos {

    public record CreatePostRequest(@NotBlank String userId, @NotBlank String title, @NotBlank String content) {
    }
}
