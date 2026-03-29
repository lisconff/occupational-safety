package com.zhituan.backend.controller;

import com.zhituan.backend.common.api.ApiResponse;
import com.zhituan.backend.domain.model.forum.ForumPost;
import com.zhituan.backend.dto.ForumDtos;
import com.zhituan.backend.service.ForumService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @PostMapping("/posts")
    public ApiResponse<ForumPost> createPost(@Valid @RequestBody ForumDtos.CreatePostRequest request) {
        return ApiResponse.ok("发帖成功", forumService.createPost(request));
    }

    @GetMapping("/posts")
    public ApiResponse<List<ForumPost>> listPosts() {
        return ApiResponse.ok(forumService.listPosts());
    }

    @GetMapping("/posts/{postId}")
    public ApiResponse<ForumPost> getPostDetail(@PathVariable String postId) {
        return ApiResponse.ok(forumService.getPostDetail(postId));
    }
}
