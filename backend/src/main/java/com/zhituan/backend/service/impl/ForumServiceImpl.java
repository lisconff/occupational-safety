package com.zhituan.backend.service.impl;

import com.zhituan.backend.domain.model.forum.ForumPost;
import com.zhituan.backend.domain.model.forum.ForumPostAttachment;
import com.zhituan.backend.domain.model.forum.ForumPostComment;
import com.zhituan.backend.domain.model.forum.ForumPostFavorite;
import com.zhituan.backend.domain.model.forum.ForumPostLike;
import com.zhituan.backend.domain.model.user.UserProfile;
import com.zhituan.backend.domain.model.user.User;
import com.zhituan.backend.dto.ForumDtos;
import com.zhituan.backend.repository.forum.ForumPostAttachmentRepository;
import com.zhituan.backend.repository.forum.ForumPostCommentRepository;
import com.zhituan.backend.repository.forum.ForumPostFavoriteRepository;
import com.zhituan.backend.repository.forum.ForumPostLikeRepository;
import com.zhituan.backend.repository.forum.ForumPostRepository;
import com.zhituan.backend.repository.user.UserRepository;
import com.zhituan.backend.repository.user.UserProfileRepository;
import com.zhituan.backend.service.ForumService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ForumServiceImpl implements ForumService {

    private final ForumPostRepository forumPostRepository;
    private final ForumPostLikeRepository forumPostLikeRepository;
    private final ForumPostFavoriteRepository forumPostFavoriteRepository;
    private final ForumPostCommentRepository forumPostCommentRepository;
    private final ForumPostAttachmentRepository forumPostAttachmentRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final Path forumUploadDir;

    public ForumServiceImpl(
            ForumPostRepository forumPostRepository,
            ForumPostLikeRepository forumPostLikeRepository,
            ForumPostFavoriteRepository forumPostFavoriteRepository,
            ForumPostCommentRepository forumPostCommentRepository,
            ForumPostAttachmentRepository forumPostAttachmentRepository,
                UserProfileRepository userProfileRepository,
            UserRepository userRepository,
            @Value("${forum.upload-dir:uploads/forum-posts}") String uploadDir
    ) {
        this.forumPostRepository = forumPostRepository;
        this.forumPostLikeRepository = forumPostLikeRepository;
        this.forumPostFavoriteRepository = forumPostFavoriteRepository;
        this.forumPostCommentRepository = forumPostCommentRepository;
        this.forumPostAttachmentRepository = forumPostAttachmentRepository;
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.forumUploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.forumUploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("无法创建论坛附件目录", e);
        }
    }

    @Override
    public ForumPost createPost(ForumDtos.CreatePostRequest request) {
        ForumPost post = ForumPost.builder()
                .userId(normalizeUserId(request.userId()))
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
    @Transactional
    public ForumPost createPost(ForumDtos.CreatePostRequest request, List<MultipartFile> attachments) {
        ForumPost post = createPost(request);
        saveAttachments(post.getPostId(), attachments);
        post.setAttachments(loadAttachmentViews(post.getPostId()));
        return post;
    }

    @Override
    public List<ForumPost> listPosts() {
        return forumPostRepository.findTop20ByOrderByCreatedAtDesc();
    }

    @Override
    public List<ForumPost> listPostsByUser(String userId) {
        String normalizedUserId = normalizeUserId(userId);
        return forumPostRepository.findByUserIdOrderByCreatedAtDesc(normalizedUserId).stream()
                .peek(post -> post.setAttachments(loadAttachmentViews(post.getPostId())))
                .toList();
    }

    @Override
    public List<ForumPost> listLikedPostsByUser(String userId) {
        String normalizedUserId = normalizeUserId(userId);
        List<ForumPostLike> likes = forumPostLikeRepository.findByUserIdOrderByCreatedAtDesc(normalizedUserId);
        if (likes.isEmpty()) {
            return List.of();
        }
        Map<String, ForumPost> postById = forumPostRepository.findAllById(
                likes.stream().map(ForumPostLike::getPostId).toList()
            ).stream()
            .collect(Collectors.toMap(ForumPost::getPostId, Function.identity()));

        return likes.stream()
            .map(item -> postById.get(item.getPostId()))
                .filter(post -> post != null)
                .peek(post -> post.setAttachments(loadAttachmentViews(post.getPostId())))
                .toList();
    }

    @Override
    public List<ForumPost> listFavoritedPostsByUser(String userId) {
        String normalizedUserId = normalizeUserId(userId);
        List<ForumPostFavorite> favorites = forumPostFavoriteRepository.findByUserIdOrderByCreatedAtDesc(normalizedUserId);
        if (favorites.isEmpty()) {
            return List.of();
        }
        Map<String, ForumPost> postById = forumPostRepository.findAllById(
                favorites.stream().map(ForumPostFavorite::getPostId).toList()
            ).stream()
            .collect(Collectors.toMap(ForumPost::getPostId, Function.identity()));

        return favorites.stream()
            .map(item -> postById.get(item.getPostId()))
                .filter(post -> post != null)
                .peek(post -> post.setAttachments(loadAttachmentViews(post.getPostId())))
                .toList();
    }

    @Override
    public ForumPost getPostDetail(String postId) {
        ForumPost post = forumPostRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("帖子不存在"));
        post.setAttachments(loadAttachmentViews(postId));
        return post;
    }

    @Override
    @Transactional
    public ForumDtos.PostInteractionResponse toggleLike(String postId, ForumDtos.PostActionRequest request) {
        ForumPost post = getExistingPost(postId);
        String userId = normalizeUserId(request.userId());

        boolean liked;
        if (forumPostLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            forumPostLikeRepository.deleteByPostIdAndUserId(postId, userId);
            liked = false;
        } else {
            forumPostLikeRepository.save(ForumPostLike.builder()
                    .postId(postId)
                    .userId(userId)
                    .createdAt(LocalDateTime.now())
                    .build());
            liked = true;
        }
        refreshCounters(post);
        boolean favorited = forumPostFavoriteRepository.existsByPostIdAndUserId(postId, userId);
        return toInteractionResponse(post, liked, favorited);
    }

    @Override
    @Transactional
    public ForumDtos.PostInteractionResponse toggleFavorite(String postId, ForumDtos.PostActionRequest request) {
        ForumPost post = getExistingPost(postId);
        String userId = normalizeUserId(request.userId());

        boolean favorited;
        if (forumPostFavoriteRepository.existsByPostIdAndUserId(postId, userId)) {
            forumPostFavoriteRepository.deleteByPostIdAndUserId(postId, userId);
            favorited = false;
        } else {
            forumPostFavoriteRepository.save(ForumPostFavorite.builder()
                    .postId(postId)
                    .userId(userId)
                    .createdAt(LocalDateTime.now())
                    .build());
            favorited = true;
        }
        refreshCounters(post);
        boolean liked = forumPostLikeRepository.existsByPostIdAndUserId(postId, userId);
        return toInteractionResponse(post, liked, favorited);
    }

    @Override
    @Transactional
    public ForumDtos.ForumCommentResponse createComment(String postId, ForumDtos.CreateCommentRequest request) {
        ForumPost post = getExistingPost(postId);
        String userId = normalizeUserId(request.userId());
        String parentCommentId = normalizeCommentId(request.parentCommentId());
        String replyToUserId = normalizeUserIdOrNull(request.replyToUserId());
        if (StringUtils.hasText(parentCommentId)) {
            ForumPostComment parent = forumPostCommentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new IllegalArgumentException("父评论不存在"));
            if (!postId.equals(parent.getPostId())) {
                throw new IllegalArgumentException("回复的评论不属于当前帖子");
            }
            if (!StringUtils.hasText(replyToUserId)) {
                replyToUserId = parent.getUserId();
            }
            parentCommentId = resolveRootCommentId(parentCommentId);
        }
        ForumPostComment comment = forumPostCommentRepository.save(ForumPostComment.builder()
                .postId(postId)
                .userId(userId)
                .parentCommentId(parentCommentId)
                .replyToUserId(replyToUserId)
                .content(request.content().trim())
                .createdAt(LocalDateTime.now())
                .build());
        refreshCounters(post);
            Map<String, UserIdentity> identityMap = loadUserIdentityMap(List.of(comment));
            return toCommentResponse(comment, identityMap);
    }

    @Override
    @Transactional
    public void deleteComment(String commentId, String userId) {
        String normalizedUserId = normalizeUserId(userId);
        ForumPostComment comment = forumPostCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在"));
        String commentOwnerId = normalizeUserId(comment.getUserId());
        if (!commentOwnerId.equals(normalizedUserId)) {
            throw new IllegalArgumentException("只能删除自己的评论");
        }
        if (!StringUtils.hasText(comment.getParentCommentId())) {
            forumPostCommentRepository.deleteByParentCommentId(commentId);
        }
        forumPostCommentRepository.delete(comment);
        refreshCounters(getExistingPost(comment.getPostId()));
    }

    @Override
    @Transactional
    public void deletePost(String postId, String userId) {
        String normalizedUserId = normalizeUserId(userId);
        ForumPost post = getExistingPost(postId);
        String postOwnerId = normalizeUserId(post.getUserId());
        if (!postOwnerId.equals(normalizedUserId)) {
            throw new IllegalArgumentException("只能删除自己的帖子");
        }
        deleteAttachmentFiles(postId);
        forumPostLikeRepository.deleteByPostId(postId);
        forumPostFavoriteRepository.deleteByPostId(postId);
        forumPostCommentRepository.deleteByPostId(postId);
        forumPostAttachmentRepository.deleteByPostId(postId);
        forumPostRepository.delete(post);
    }

    @Override
    public ForumPostAttachment getAttachment(String attachmentId) {
        return forumPostAttachmentRepository.findByAttachmentId(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("附件不存在"));
    }

    private ForumPost getExistingPost(String postId) {
        return forumPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("帖子不存在"));
    }

    private void refreshCounters(ForumPost post) {
        String postId = post.getPostId();
        post.setLikeCount((int) forumPostLikeRepository.countByPostId(postId));
        post.setFavoriteCount((int) forumPostFavoriteRepository.countByPostId(postId));
        post.setCommentCount((int) forumPostCommentRepository.countByPostId(postId));
        post.setUpdatedAt(LocalDateTime.now());
        forumPostRepository.save(post);
    }

    private ForumDtos.PostInteractionResponse toInteractionResponse(ForumPost post, boolean liked, boolean favorited) {
        return new ForumDtos.PostInteractionResponse(
                post.getPostId(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getFavoriteCount(),
                liked,
                favorited
        );
    }

    private ForumDtos.ForumCommentResponse toCommentResponse(ForumPostComment comment, Map<String, UserIdentity> identityMap) {
        UserIdentity author = identityMap.getOrDefault(comment.getUserId(), UserIdentity.anonymous(comment.getUserId()));
        UserIdentity replyTo = identityMap.getOrDefault(comment.getReplyToUserId(), UserIdentity.anonymous(comment.getReplyToUserId()));
        return new ForumDtos.ForumCommentResponse(
                comment.getCommentId(),
                comment.getPostId(),
                comment.getUserId(),
                author.displayName(),
                author.avatarDataUrl(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getParentCommentId(),
                comment.getReplyToUserId(),
                replyTo.displayName(),
                List.of()
        );
    }

    @Override
    public List<ForumDtos.ForumCommentResponse> listComments(String postId) {
        return buildCommentTree(postId);
    }

    private List<ForumDtos.ForumCommentResponse> buildCommentTree(String postId) {
        getExistingPost(postId);
        List<ForumPostComment> comments = forumPostCommentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        Map<String, UserIdentity> identityMap = loadUserIdentityMap(comments);

        List<ForumCommentRow> rows = comments.stream()
            .map(comment -> new ForumCommentRow(comment, toCommentResponse(comment, identityMap)))
                .toList();
        Map<String, ForumDtos.ForumCommentResponse> roots = new HashMap<>();
        Map<String, List<ForumDtos.ForumCommentResponse>> repliesByRoot = new HashMap<>();

        for (ForumCommentRow row : rows) {
            ForumPostComment comment = row.comment();
            ForumDtos.ForumCommentResponse response = row.response();
            if (!StringUtils.hasText(comment.getParentCommentId())) {
                roots.put(comment.getCommentId(), new ForumDtos.ForumCommentResponse(
                    response.commentId(), response.postId(), response.userId(), response.username(), response.avatarDataUrl(), response.content(), response.createdAt(),
                        null, response.replyToUserId(), response.replyToUsername(), new ArrayList<>()
                ));
            } else {
                String rootId = comment.getParentCommentId();
                repliesByRoot.computeIfAbsent(rootId, key -> new ArrayList<>()).add(new ForumDtos.ForumCommentResponse(
                    response.commentId(), response.postId(), response.userId(), response.username(), response.avatarDataUrl(), response.content(), response.createdAt(),
                        rootId, response.replyToUserId(), response.replyToUsername(), new ArrayList<>()
                ));
            }
        }

        List<ForumDtos.ForumCommentResponse> tree = new ArrayList<>();
        for (Map.Entry<String, ForumDtos.ForumCommentResponse> entry : roots.entrySet()) {
            List<ForumDtos.ForumCommentResponse> replies = repliesByRoot.getOrDefault(entry.getKey(), List.of());
            tree.add(new ForumDtos.ForumCommentResponse(
                    entry.getValue().commentId(),
                    entry.getValue().postId(),
                    entry.getValue().userId(),
                    entry.getValue().username(),
                    entry.getValue().avatarDataUrl(),
                    entry.getValue().content(),
                    entry.getValue().createdAt(),
                    null,
                    entry.getValue().replyToUserId(),
                    entry.getValue().replyToUsername(),
                    replies
            ));
        }
        tree.sort((a, b) -> a.createdAt().compareTo(b.createdAt()));
        return tree;
    }

    private record ForumCommentRow(ForumPostComment comment, ForumDtos.ForumCommentResponse response) {
    }

    private String resolveRootCommentId(String commentId) {
        ForumPostComment comment = forumPostCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在"));
        if (!StringUtils.hasText(comment.getParentCommentId())) {
            return comment.getCommentId();
        }
        return comment.getParentCommentId();
    }

    private String normalizeCommentId(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String normalizeText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String normalizeUserIdOrNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return normalizeUserId(value);
    }

    private String normalizeUserId(String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("userId 不能为空");
        }
        String candidate = value.trim();
        if (userRepository.existsById(candidate)) {
            return candidate;
        }
        User user = userRepository.findByUsername(candidate).orElse(null);
        if (user != null) {
            return user.getUserId();
        }
        return candidate;
    }

    private Map<String, UserIdentity> loadUserIdentityMap(List<ForumPostComment> comments) {
        Set<String> keys = comments.stream()
                .flatMap(comment -> java.util.stream.Stream.of(comment.getUserId(), comment.getReplyToUserId()))
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.toSet());

        Map<String, UserIdentity> map = new HashMap<>();
        for (String key : keys) {
            map.put(key, resolveUserIdentity(key));
        }
        return map;
    }

    private UserIdentity resolveUserIdentity(String rawUserKey) {
        if (!StringUtils.hasText(rawUserKey)) {
            return UserIdentity.anonymous(rawUserKey);
        }

        String key = rawUserKey.trim();
        User byId = userRepository.findById(key).orElse(null);
        if (byId != null) {
            return new UserIdentity(byId.getUserId(), byId.getUsername(), loadUserAvatarByUserId(byId.getUserId()));
        }

        User byUsername = userRepository.findByUsername(key).orElse(null);
        if (byUsername != null) {
            return new UserIdentity(byUsername.getUserId(), byUsername.getUsername(), loadUserAvatarByUserId(byUsername.getUserId()));
        }

        UserProfile profile = userProfileRepository.findById(key).orElse(null);
        return new UserIdentity(key, key, profile == null ? null : profile.getAvatarDataUrl());
    }

    private String loadUserAvatarByUserId(String userId) {
        if (!StringUtils.hasText(userId)) {
            return null;
        }
        UserProfile profile = userProfileRepository.findById(userId).orElse(null);
        if (profile == null) {
            return null;
        }
        return StringUtils.hasText(profile.getAvatarDataUrl()) ? profile.getAvatarDataUrl() : null;
    }

    private void saveAttachments(String postId, List<MultipartFile> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }

        List<ForumPostAttachment> savedAttachments = new ArrayList<>();
        for (MultipartFile file : attachments) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            validateAttachment(file);
            String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "attachment" : file.getOriginalFilename());
            String storedName = UUID.randomUUID() + "_" + sanitizeFileName(originalName);
            Path target = forumUploadDir.resolve(storedName).normalize();
            try {
                Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IllegalArgumentException("附件保存失败: " + e.getMessage());
            }

            ForumPostAttachment attachment = forumPostAttachmentRepository.save(ForumPostAttachment.builder()
                    .postId(postId)
                    .originalName(originalName)
                    .storedName(storedName)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .filePath(target.toString())
                    .createdAt(LocalDateTime.now())
                    .build());
            savedAttachments.add(attachment);
        }

        if (!savedAttachments.isEmpty()) {
            refreshCounters(getExistingPost(postId));
        }
    }

    private void validateAttachment(MultipartFile file) {
        final long maxBytes = 10L * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException("附件大小不能超过 10MB");
        }
        String originalName = file.getOriginalFilename();
        String ext = extensionOf(originalName);
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        boolean image = contentType.startsWith("image/") || isImageExtension(ext);
        boolean document = isDocumentExtension(ext);
        if (!image && !document) {
            throw new IllegalArgumentException("仅支持上传图片或文档文件（jpg/png/webp/gif/pdf/doc/docx/txt/md）");
        }
    }

    private boolean isImageExtension(String ext) {
        return "jpg".equals(ext) || "jpeg".equals(ext) || "png".equals(ext) || "gif".equals(ext) || "webp".equals(ext);
    }

    private boolean isDocumentExtension(String ext) {
        return "pdf".equals(ext) || "doc".equals(ext) || "docx".equals(ext) || "txt".equals(ext) || "md".equals(ext);
    }

    private String extensionOf(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private String sanitizeFileName(String originalName) {
        return originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private List<ForumDtos.ForumAttachmentView> loadAttachmentViews(String postId) {
        return forumPostAttachmentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::toAttachmentView)
                .toList();
    }

    private ForumDtos.ForumAttachmentView toAttachmentView(ForumPostAttachment attachment) {
        String contentType = attachment.getContentType() == null ? "" : attachment.getContentType();
        boolean image = contentType.startsWith("image/");
        return new ForumDtos.ForumAttachmentView(
                attachment.getAttachmentId(),
                attachment.getOriginalName(),
                contentType,
                attachment.getFileSize(),
                "/api/forum/attachments/" + attachment.getAttachmentId(),
                image
        );
    }

    private void deleteAttachmentFiles(String postId) {
        forumPostAttachmentRepository.findByPostIdOrderByCreatedAtAsc(postId).forEach(attachment -> {
            try {
                Files.deleteIfExists(Path.of(attachment.getFilePath()));
            } catch (IOException ignored) {
            }
        });
    }

    private record UserIdentity(String userId, String displayName, String avatarDataUrl) {
        static UserIdentity anonymous(String raw) {
            return new UserIdentity(raw, raw, null);
        }
    }
}
