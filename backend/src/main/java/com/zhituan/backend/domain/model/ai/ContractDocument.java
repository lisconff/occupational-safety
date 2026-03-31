package com.zhituan.backend.domain.model.ai;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contract_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String documentId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String sourceType; // TEXT / FILE

    @Column(columnDefinition = "TEXT")
    private String originalText;

    private String fileName;
    
    private String fileUrl;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    // --- 领域行为方法 (Rich Domain Model) ---

    /**
     * 更新文档来源与内容
     */
    public void updateSource(String sourceType, String originalText, String fileName, String fileUrl) {
        this.sourceType = sourceType;
        this.originalText = originalText;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.uploadedAt = LocalDateTime.now();
    }

    /**
     * 获取纯文本用于AI分析（无论来源是什么）
     */
    public String getPlainText() {
        // 如果系统后续需要解析文件 URL 中的文字，相关逻辑可在此抽象
        return this.originalText != null ? this.originalText.trim() : "";
    }

    /**
     * 校验上传的合法性
     */
    public boolean validateUpload() {
        if ("TEXT".equalsIgnoreCase(this.sourceType)) {
            return this.originalText != null && !this.originalText.trim().isEmpty();
        } else if ("FILE".equalsIgnoreCase(this.sourceType)) {
            return this.fileUrl != null && !this.fileUrl.trim().isEmpty();
        }
        return false;
    }
}
