package com.zhituan.backend.repository.forum;

import com.zhituan.backend.domain.model.forum.ForumPostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumPostLikeRepository extends JpaRepository<ForumPostLike, String> {
    long countByPostId(String postId);

    List<ForumPostLike> findByUserIdOrderByCreatedAtDesc(String userId);

    boolean existsByPostIdAndUserId(String postId, String userId);

    void deleteByPostIdAndUserId(String postId, String userId);

    void deleteByPostId(String postId);
}
