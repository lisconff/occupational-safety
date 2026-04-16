package com.zhituan.backend.domain.model.forum;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "forum_post_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumPostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String commentId;

    private String postId;
    private String userId;
    private String parentCommentId;
    private String replyToUserId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;
}
