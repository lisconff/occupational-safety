package com.zhituan.backend.service;

import com.zhituan.backend.domain.model.forum.ForumPost;
import com.zhituan.backend.dto.ForumDtos;

import java.util.List;

public interface ForumService {
    ForumPost createPost(ForumDtos.CreatePostRequest request);

    List<ForumPost> listPosts();

    ForumPost getPostDetail(String postId);
}
