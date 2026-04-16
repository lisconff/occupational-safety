package com.zhituan.backend.repository.forum;

import com.zhituan.backend.domain.model.forum.ForumPostAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ForumPostAttachmentRepository extends JpaRepository<ForumPostAttachment, String> {
    List<ForumPostAttachment> findByPostIdOrderByCreatedAtAsc(String postId);

    Optional<ForumPostAttachment> findByAttachmentId(String attachmentId);

    void deleteByPostId(String postId);
}
