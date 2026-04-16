package com.zhituan.backend.repository.forum;

import com.zhituan.backend.domain.model.forum.ForumPostFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumPostFavoriteRepository extends JpaRepository<ForumPostFavorite, String> {
    long countByPostId(String postId);

    List<ForumPostFavorite> findByUserIdOrderByCreatedAtDesc(String userId);

    boolean existsByPostIdAndUserId(String postId, String userId);

    void deleteByPostIdAndUserId(String postId, String userId);

    void deleteByPostId(String postId);
}
