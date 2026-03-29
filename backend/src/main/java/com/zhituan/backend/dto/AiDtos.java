package com.zhituan.backend.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class AiDtos {

    public record AnalyzeRequest(@NotBlank String userId, @NotBlank String text) {
    }

    public record RiskItemView(String matchedText, String riskLevel, String reason, String suggestion) {
    }

    public record AnalysisReportView(
            String reportId,
            String businessType,
            Integer score,
            String overallRiskLevel,
            List<RiskItemView> riskItems
    ) {
    }
}
