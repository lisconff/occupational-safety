package com.zhituan.backend.domain.model.ai;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "risk_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String ruleId;

    @Column(nullable = false, unique = true)
    private String ruleCode;

    @Column(nullable = false)
    private String ruleName;

    @Column(nullable = false)
    private String category; // 劳动合同 / 黑话识别 / 岗位风险

    @Column(nullable = false)
    private String riskLevel; // LOW / MEDIUM / HIGH

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "risk_rule_keywords", joinColumns = @JoinColumn(name = "rule_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    @Column(columnDefinition = "TEXT")
    private String legalBasis;

    @Column(columnDefinition = "TEXT")
    private String suggestionTemplate;

    @Column(nullable = false)
    private Boolean enabled;

    // --- 领域行为方法 (Rich Domain Model) ---

    /**
     * 规则匹配逻辑：检查输入文本是否触发当前规则的关键词
     */
    public boolean match(String text) {
        if (!Boolean.TRUE.equals(this.enabled) || text == null || keywords == null || keywords.isEmpty()) {
            return false;
        }
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true; // 只要命中一个关键词即视为触发
            }
        }
        return false;
    }

    /**
     * 根据当前规则定义评估风险等级（可预留结合hitCount升级风险逻辑）
     */
    public String evaluateRiskLevel(int hitCount) {
        // 如果命中词频非常高，可以将原来 MEDIUM 的动态升级为 HIGH
        if (hitCount > 3 && "MEDIUM".equals(this.riskLevel)) {
            return "HIGH";
        }
        return this.riskLevel;
    }

    /**
     * 维护规则配置
     */
    public void updateRuleConfig(String ruleName, List<String> keywords, String legalBasis, String suggestionTemplate, Boolean enabled) {
        this.ruleName = ruleName;
        this.keywords = keywords;
        this.legalBasis = legalBasis;
        this.suggestionTemplate = suggestionTemplate;
        this.enabled = enabled;
    }
}
