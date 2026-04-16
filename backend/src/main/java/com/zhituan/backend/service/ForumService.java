package com.zhituan.backend.service;

import com.zhituan.backend.domain.model.forum.ForumPost;
import com.zhituan.backend.domain.model.forum.ForumPostAttachment;
import com.zhituan.backend.dto.ForumDtos;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ForumService {
    ForumPost createPost(ForumDtos.CreatePostRequest request);

    ForumPost createPost(ForumDtos.CreatePostRequest request, List<MultipartFile> attachments);

    List<ForumPost> listPosts();

    List<ForumPost> listPostsByUser(String userId);

    List<ForumPost> listLikedPostsByUser(String userId);

    List<ForumPost> listFavoritedPostsByUser(String userId);

    ForumPost getPostDetail(String postId);

    ForumDtos.PostInteractionResponse toggleLike(String postId, ForumDtos.PostActionRequest request);

    ForumDtos.PostInteractionResponse toggleFavorite(String postId, ForumDtos.PostActionRequest request);

    ForumDtos.ForumCommentResponse createComment(String postId, ForumDtos.CreateCommentRequest request);

    List<ForumDtos.ForumCommentResponse> listComments(String postId);

    void deleteComment(String commentId, String userId);

    void deletePost(String postId, String userId);

    ForumPostAttachment getAttachment(String attachmentId);
}
