package com.zhituan.backend.controller;

import com.zhituan.backend.common.api.ApiResponse;
import com.zhituan.backend.domain.model.forum.ForumPostAttachment;
import com.zhituan.backend.domain.model.forum.ForumPost;
import com.zhituan.backend.dto.ForumDtos;
import com.zhituan.backend.service.ForumService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ForumPost> createPostWithAttachments(
            @RequestParam("userId") String userId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        return ApiResponse.ok("发帖成功", forumService.createPost(new ForumDtos.CreatePostRequest(userId, title, content), attachments));
    }

    @GetMapping("/posts")
    public ApiResponse<List<ForumPost>> listPosts() {
        return ApiResponse.ok(forumService.listPosts());
    }

    @GetMapping("/users/{userId}/posts")
    public ApiResponse<List<ForumPost>> listPostsByUser(@PathVariable String userId) {
        return ApiResponse.ok(forumService.listPostsByUser(userId));
    }

    @GetMapping("/users/{userId}/likes")
    public ApiResponse<List<ForumPost>> listLikedPostsByUser(@PathVariable String userId) {
        return ApiResponse.ok(forumService.listLikedPostsByUser(userId));
    }

    @GetMapping("/users/{userId}/favorites")
    public ApiResponse<List<ForumPost>> listFavoritedPostsByUser(@PathVariable String userId) {
        return ApiResponse.ok(forumService.listFavoritedPostsByUser(userId));
    }

    @GetMapping("/posts/{postId}")
    public ApiResponse<ForumPost> getPostDetail(@PathVariable String postId) {
        return ApiResponse.ok(forumService.getPostDetail(postId));
    }

    @PostMapping("/posts/{postId}/like")
    public ApiResponse<ForumDtos.PostInteractionResponse> toggleLike(
            @PathVariable String postId,
            @Valid @RequestBody ForumDtos.PostActionRequest request
    ) {
        return ApiResponse.ok("点赞状态已更新", forumService.toggleLike(postId, request));
    }

    @PostMapping("/posts/{postId}/favorite")
    public ApiResponse<ForumDtos.PostInteractionResponse> toggleFavorite(
            @PathVariable String postId,
            @Valid @RequestBody ForumDtos.PostActionRequest request
    ) {
        return ApiResponse.ok("收藏状态已更新", forumService.toggleFavorite(postId, request));
    }

    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<List<ForumDtos.ForumCommentResponse>> listComments(@PathVariable String postId) {
        return ApiResponse.ok(forumService.listComments(postId));
    }

    @PostMapping("/posts/{postId}/comments")
    public ApiResponse<ForumDtos.ForumCommentResponse> createComment(
            @PathVariable String postId,
            @Valid @RequestBody ForumDtos.CreateCommentRequest request
    ) {
        return ApiResponse.ok("评论成功", forumService.createComment(postId, request));
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(
            @PathVariable String commentId,
            @RequestParam String userId
    ) {
        forumService.deleteComment(commentId, userId);
        return ApiResponse.ok("删除成功", null);
    }

    @GetMapping("/attachments/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable String attachmentId,
            @RequestParam(value = "download", defaultValue = "false") boolean download
    ) {
        ForumPostAttachment attachment = forumService.getAttachment(attachmentId);
        FileSystemResource resource = new FileSystemResource(attachment.getFilePath());
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        long contentLength;
        try {
            contentLength = attachment.getFileSize() == null ? resource.contentLength() : attachment.getFileSize();
        } catch (IOException e) {
            contentLength = attachment.getFileSize() == null ? 0L : attachment.getFileSize();
        }
        ContentDisposition disposition = download
            ? ContentDisposition.attachment().filename(attachment.getOriginalName()).build()
            : ContentDisposition.inline().filename(attachment.getOriginalName()).build();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.parseMediaType(attachment.getContentType() == null || attachment.getContentType().isBlank() ? MediaType.APPLICATION_OCTET_STREAM_VALUE : attachment.getContentType()))
                .contentLength(contentLength)
                .body(resource);
    }

    @DeleteMapping("/posts/{postId}")
    public ApiResponse<Void> deletePost(
            @PathVariable String postId,
            @RequestParam String userId
    ) {
        forumService.deletePost(postId, userId);
        return ApiResponse.ok("删除成功", null);
    }
}
