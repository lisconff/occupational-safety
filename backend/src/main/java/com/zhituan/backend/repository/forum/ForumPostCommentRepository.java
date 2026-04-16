package com.zhituan.backend.repository.forum;

import com.zhituan.backend.domain.model.forum.ForumPostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumPostCommentRepository extends JpaRepository<ForumPostComment, String> {
    long countByPostId(String postId);

    List<ForumPostComment> findByPostIdOrderByCreatedAtAsc(String postId);

    List<ForumPostComment> findByPostIdAndParentCommentIdOrderByCreatedAtAsc(String postId, String parentCommentId);

    List<ForumPostComment> findByParentCommentIdOrderByCreatedAtAsc(String parentCommentId);

    List<ForumPostComment> findByUserIdOrderByCreatedAtDesc(String userId);

    void deleteByPostId(String postId);

    void deleteByParentCommentId(String parentCommentId);
}
