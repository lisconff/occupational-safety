package com.zhituan.backend.repository.forum;

import com.zhituan.backend.domain.model.forum.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumPostRepository extends JpaRepository<ForumPost, String> {
    List<ForumPost> findTop20ByOrderByCreatedAtDesc();
}
