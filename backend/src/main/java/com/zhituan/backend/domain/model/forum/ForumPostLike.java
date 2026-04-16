package com.zhituan.backend.domain.model.forum;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "forum_post_likes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_forum_like_post_user", columnNames = {"postId", "userId"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumPostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String postId;
    private String userId;
    private LocalDateTime createdAt;
}
