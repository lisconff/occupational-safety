package com.zhituan.backend.domain.model.forum;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "forum_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumPost {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String postId;

    private String userId;
    private String title;

    @Column(length = 5000)
    private String content;

    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
