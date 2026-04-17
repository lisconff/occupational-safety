package com.zhituan.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class ForumDtos {

    public record CreatePostRequest(@NotBlank String userId, @NotBlank String title, @NotBlank String content) {
    }

    public record PostActionRequest(@NotBlank String userId) {
    }

        public record CreateCommentRequest(@NotBlank String userId, @NotBlank String content, String parentCommentId, String replyToUserId) {
    }

    public record PostInteractionResponse(
            String postId,
            Integer likeCount,
            Integer commentCount,
            Integer favoriteCount,
            Boolean liked,
            Boolean favorited
    ) {
    }

    public record ForumCommentResponse(
            String commentId,
            String postId,
            String userId,
            String username,
            String avatarDataUrl,
            String content,
            java.time.LocalDateTime createdAt,
            String parentCommentId,
            String replyToUserId,
            String replyToUsername,
            java.util.List<ForumCommentResponse> replies
    ) {
    }

    public record ForumAttachmentView(
            String attachmentId,
            String originalName,
            String contentType,
            Long fileSize,
            String downloadUrl,
            Boolean image
    ) {
    }
}
