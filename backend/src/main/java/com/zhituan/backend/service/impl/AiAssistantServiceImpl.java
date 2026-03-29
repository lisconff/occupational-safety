package com.zhituan.backend.service.impl;

import com.zhituan.backend.domain.model.ai.AnalysisReport;
import com.zhituan.backend.domain.model.ai.RiskItem;
import com.zhituan.backend.dto.AiDtos;
import com.zhituan.backend.repository.ai.AnalysisReportRepository;
import com.zhituan.backend.repository.ai.RiskItemRepository;
import com.zhituan.backend.service.AiAssistantService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AiAssistantServiceImpl implements AiAssistantService {

    private final AnalysisReportRepository analysisReportRepository;
    private final RiskItemRepository riskItemRepository;

    public AiAssistantServiceImpl(AnalysisReportRepository analysisReportRepository, RiskItemRepository riskItemRepository) {
        this.analysisReportRepository = analysisReportRepository;
        this.riskItemRepository = riskItemRepository;
    }

    @Override
    public AiDtos.AnalysisReportView analyzeContract(AiDtos.AnalyzeRequest request) {
        return analyzeByType("CONTRACT", request);
    }

    @Override
    public AiDtos.AnalysisReportView detectBlackSlang(AiDtos.AnalyzeRequest request) {
        return analyzeByType("BLACK_SLANG", request);
    }

    @Override
    public AiDtos.AnalysisReportView evaluateJobRisk(AiDtos.AnalyzeRequest request) {
        return analyzeByType("JOB_RISK", request);
    }

    @Override
    public List<AiDtos.AnalysisReportView> listHistory(String userId) {
        return analysisReportRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(report -> new AiDtos.AnalysisReportView(
                        report.getReportId(),
                        report.getBusinessType(),
                        report.getScore(),
                        report.getOverallRiskLevel(),
                        riskItemRepository.findByReportId(report.getReportId()).stream()
                                .map(item -> new AiDtos.RiskItemView(
                                        item.getMatchedText(),
                                        item.getRiskLevel(),
                                        item.getReason(),
                                        item.getSuggestion()
                                ))
                                .toList()
                ))
                .toList();
    }

    private AiDtos.AnalysisReportView analyzeByType(String businessType, AiDtos.AnalyzeRequest request) {
        int score = Math.min(95, Math.max(5, request.text().length() % 100));
        String riskLevel = score >= 80 ? "HIGH" : score >= 50 ? "MEDIUM" : "LOW";

        AnalysisReport report = AnalysisReport.builder()
                .userId(request.userId())
                .businessType(businessType)
                .inputSummary(request.text().substring(0, Math.min(60, request.text().length())))
                .score(score)
                .overallRiskLevel(riskLevel)
                .createdAt(LocalDateTime.now())
                .build();

        AnalysisReport saved = analysisReportRepository.save(report);

        RiskItem item = RiskItem.builder()
                .reportId(saved.getReportId())
                .matchedText(request.text().substring(0, Math.min(20, request.text().length())))
                .startIndex(0)
                .endIndex(Math.min(20, request.text().length()))
                .riskLevel(riskLevel)
                .reason("命中了基础演示规则")
                .legalBasis("后续按规则库填充")
                .suggestion("请结合平台建议进行修改")
                .build();

        RiskItem savedItem = riskItemRepository.save(item);

        return new AiDtos.AnalysisReportView(
                saved.getReportId(),
                saved.getBusinessType(),
                saved.getScore(),
                saved.getOverallRiskLevel(),
                List.of(new AiDtos.RiskItemView(
                        savedItem.getMatchedText(),
                        savedItem.getRiskLevel(),
                        savedItem.getReason(),
                        savedItem.getSuggestion()
                ))
        );
    }
}
