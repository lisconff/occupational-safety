package com.zhituan.backend.domain.model.ai;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "risk_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String riskItemId;

    @Column(nullable = false)
    private String reportId;

    @Column(columnDefinition = "TEXT")
    private String matchedText;

    private Integer startIndex;
    private Integer endIndex;
    private String riskLevel;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String legalBasis;

    @Column(columnDefinition = "TEXT")
    private String suggestion;

    // --- 领域行为方法 (Rich Domain Model) ---

    /**
     * 更新导致高亮的原文起止下标
     */
    public void updateSpan(Integer startIndex, Integer endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    /**
     * 丰富风险处理建议
     */
    public void updateReason(String reason, String legalBasis, String suggestion) {
        this.reason = reason;
        this.legalBasis = legalBasis;
        this.suggestion = suggestion;
    }

    /**
     * 返回用于前端高亮展示的段落信息（格式化）
     */
    public String toHighlightSegment() {
        return String.format("[%d-%d] %s", startIndex, endIndex, matchedText != null ? matchedText : "");
    }
}
