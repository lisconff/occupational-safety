package com.zhituan.backend.domain.model.ai;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String reportId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String businessType;

    @Column(length = 2000)
    private String inputSummary;

    private Integer score;
    private String overallRiskLevel;
    private LocalDateTime createdAt;

    // --- 领域行为方法 (Rich Domain Model) ---

    /**
     * 自动更新整体风险等级（基于评分）
     */
    public void calculateOverallRiskLevel() {
        if (this.score == null) {
            this.overallRiskLevel = "UNKNOWN";
        } else if (this.score >= 80) {
            this.overallRiskLevel = "LOW";
        } else if (this.score >= 60) {
            this.overallRiskLevel = "MEDIUM";
        } else {
            this.overallRiskLevel = "HIGH";
        }
    }

    /**
     * 完成报告生成时的收尾操作
     */
    public void finalizeReport(Integer score, String inputSummary) {
        this.score = score;
        this.inputSummary = inputSummary;
        this.createdAt = LocalDateTime.now();
        this.calculateOverallRiskLevel();
    }
}
