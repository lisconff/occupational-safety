package com.zhituan.backend.service.impl;

import com.zhituan.backend.domain.model.forum.ForumPost;
import com.zhituan.backend.dto.ForumDtos;
import com.zhituan.backend.repository.forum.ForumPostRepository;
import com.zhituan.backend.service.ForumService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ForumServiceImpl implements ForumService {

    private final ForumPostRepository forumPostRepository;

    public ForumServiceImpl(ForumPostRepository forumPostRepository) {
        this.forumPostRepository = forumPostRepository;
    }

    @Override
    public ForumPost createPost(ForumDtos.CreatePostRequest request) {
        ForumPost post = ForumPost.builder()
                .userId(request.userId())
                .title(request.title())
                .content(request.content())
                .likeCount(0)
                .commentCount(0)
                .favoriteCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return forumPostRepository.save(post);
    }

    @Override
    public List<ForumPost> listPosts() {
        return forumPostRepository.findTop20ByOrderByCreatedAtDesc();
    }

    @Override
    public ForumPost getPostDetail(String postId) {
        return forumPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("帖子不存在"));
    }
}
