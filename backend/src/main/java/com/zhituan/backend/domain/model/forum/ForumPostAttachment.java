package com.zhituan.backend.domain.model.forum;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "forum_post_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumPostAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String attachmentId;

    private String postId;
    private String originalName;
    private String storedName;
    private String contentType;
    private Long fileSize;
    private String filePath;
    private LocalDateTime createdAt;
}
